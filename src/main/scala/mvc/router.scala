package org.scalaeye

import org.scalaeye._, mvc._
import java.util.regex.Pattern
import scala.util.matching.Regex, Regex.Match
import scala.collection.mutable.ListBuffer

// MVC框架的重点之一：路由。
//
// 我们可事先定义一些规则（如在单独文件中、函数参数中、或注解中），这些规则每一条都将对应一个Router类。
// 该类将预处理规则字符串，将它转化为合适的正则表达式，当有请求时，检查自己是否可以将该请求转向，同时，
// 还会得到预定义的变量对应的值。
//
// 举例，我们先定义了以下路由规则：
//
// <code>
// any("/users/index") { action1() }
// any("/users/{id:\d+}") { action2() }
// any("/users/{id}") { action3() }
// any("/users/*") { action4()}
// </code>
//
// 如果请求的url：
// /users/index  => action1
// /users/123    => action2, 且id=123
// /users/abc    => action3, 且id=abc
// /users/aaa/bbb => action4
//
// 路由字符串的规则如下：
// 1. 不能包含空格
// 2. 必须以/开头
// 3. 变量须用{}括起来，如{id}，不能跨分隔线'/'
// 4. 如果想使用正则表达式，则使用冒号，如{id:\d+}
// 5. 结尾可使用*，表示余下所有，它只能放在结尾，紧跟在/之后，形如/*
// 6. 所有的变量（不论是否使用正则），必须要取到值，否则匹配失败
// 7. 在匹配时，规则与url最后的/都将被忽略，也就是说 /users/{id}可匹配/users/123/
//
// 存在的问题：
// 1. 只能定义非常简单的url规则（基本够用）
// 2. 正则表达式中无法使用{}
//
// 用法举例：
// <code>
// val router = new Router("/users/{id}")
// val (fit, params) = router.parse("/users/123")
// println(fit)//  -> true
// pritnln(params)//  -> Map("id"->"123")
// </code>
//
// 实现细节：
// 规则：/users/id-{id:\d+}/{title}/*
// 将会按以下方式转化为正则：
// 1. 按字符{和}分解为数组: '/users/id-', 'id:\d+', '/', 'title', '/*'
// 2. 将非变量组，用\Q和\E括起来（所有内容将不被转义）
// 3. 将非正则变量组变为 ([^/]+)
// 4. 将正则变量组变为 (regex)，其中的regex为开发者自定的正则表达式
// 5. 将末尾的/*（如果有的话），变为/?.*
// 6. 使用该正则匹配请求的url(只取url的前面，不包含参数、锚点等内容）
//
// 线程安全:
// 该类不可变，线程安全
class Router(val pattern: String, val action: Action, val method: String = "any") {

	/** 用于取得route中的变量(被包含在{}内)的正则*/
	private val varPattern = """\{([^/]+?)\}""".r

	/** 得到route中定义的变量名，如/users/{id}/{title}将得到List("id","title") */
	val paramNames: List[String] = {
		varPattern.findAllIn(pattern).matchData.map(m => {
			val varName = m.group(1).trim
			if (varName.contains(":")) {
				varName substring (0, varName.indexOf(':')) trim
			} else varName
		}).toList
	}

	/** 将route规则字符串，转换为合适的正则表达式 */
	val regexString: String = {
		// 增加\Q\E
		var x = (pattern.stripSuffix("/").split("[{}]").zipWithIndex collect {
			case (item, i) => {
				if (i % 2 == 0) """\Q"""+item+"""\E"""
				else "{"+item+"}"
			}
		}).mkString

		// 如果末尾有/*，进行调整
		if (x.endsWith("""/*\E""")) {
			x = x.stripSuffix("""/*\E""")+"""\E/?.*"""
		}

		// 去除空白的\Q\E
		x = "^"+x.replace("""\Q\E""", "")+"$"

		// 将变量定义转为正则
		varPattern.replaceAllIn(x, m => {
			val varName = m.group(1).trim
			if (varName.contains(":")) {
				val re: String = varName.substring(varName.indexOf(':') + 1).trim()
				"("+re.replace("""\""", """\\""")+")"
			} else "([^/]+)"
		}).replaceAll("/[*]$", "/?.*?")
	}

	/** 使用正则表达式字符串和变量名，生成regex对象*/
	val regexPattern: Regex = {
		new Regex(regexString, paramNames: _*)
	}

	/** 使用regex对象，解析某url。如果成功，则返回Some(params)，否则返回None */
	def parse(url: String, method: String = "any"): Option[Map[String, String]] = {
		if (this.method != "any" && this.method != method) return None
		regexPattern.findFirstMatchIn(url.stripSuffix("/")) match {
			case Some(m) => {
				val list = paramNames map (name => name -> m.group(name))
				if (list exists (_._2 == "")) None
				else Some(Map(list: _*))
			}
			case _ => None
		}
	}

	override def toString = method+" : "+pattern
}

/**
 * 可使用Rouetr("/users/{id}")的方式生成Router
 */
object Router {

	/** 用于保存route规则*/
	val routers = ListBuffer[Router]()

	/** 使用Router()来创建新Router */
	def apply(pattern: String, action: Action, method: String = "any") = new Router(pattern, action, method)

	/** 插入到列表前（优先级高）*/
	def prepend(pattern: String, action: Action, method: String) {
		val router = Router(pattern, action, method)
		router +=: routers
	}

	/** 插入到列表后（优先级低）*/
	def append(pattern: String, action: Action, method: String) {
		val router = Router(pattern, action, method)
		routers += router
	}

	/** 寻找合适的Router，如果找不到，返回None */
	def findMatch(method: String, url: String): Option[MatchData] = {
		for (router <- routers) {
			val params = router.parse(url, method.toLowerCase)
			if (params != None) {
				return Some(new MatchData(router, params.get))
			}
		}
		None
	}

	/** 得到当前routers列表的拷贝*/
	def getRouters = {
		routers ++ Nil
	}
}

/** 用于保存match结果 */
case class MatchData(router: Router, params: Map[String, String])


package org.scalaeye.mvc

import org.scalaeye._, mvc._

/** 为Flash的支持预定一些key */
object FlashMap {
	val SESSION_KEY = "scalaeye.mvc.flash"
	val MESSAGE = "scalaeye.message"
	val ERROR = "scalaeye.error"
	val DEBUG = "scalaeye.debug"
	val WARN = "scalaeye.warn"
}
import FlashMap._

/**
 * 实现Flash
 *
 * 原理：
 * 包含有两个map，一个current，一个next。current是为当前request准备的，next是为下一个request准备的。
 * 向flash中写入时，将写入到next；读取时，先读next，没有再读current
 * 当一个请求结束时，将调用reset()函数，将current丢弃，next传给current，再给next生成一个空的map
 * 这样，当下一个请求到来时，通过current还可以看到上一次请求中传入的数据
 *
 * 因为flash是跨请求的，所以需要将它放入到session中
 */
class FlashMap extends MvcContext {
	/** 保存的其实是前一次request中的数据*/
	var current = MMap[String, Any]()

	/** 保存的数据可供下一个请求使用*/
	var next = MMap[String, Any]()

	/** 取值。先读next，再读current，没有则返回"" */
	def apply(key: String): Any = {
		next.get(key) match {
			case Some(v) => v
			case _ => current.getOrElse(key, "")
		}
	}

	/** 给flash增加数据，写到next中，可供本次和下次请求使用 */
	def update(key: String, value: Any) = next(key) = value

	/** 重置数据。在每个请求结束时将调用该方法 */
	def reset = {
		current = next;
		next = MMap[String, Any]()
		session.setAttribute(SESSION_KEY, this)
	}

	/** 判断是否包含某值 */
	def contains(key: String) = apply(key) != None

	// 一些预定义的简便方法
	def message = apply(MESSAGE)
	def message(text: String) = update(MESSAGE, text)
	def error = apply(ERROR)
	def error(text: String) = update(ERROR, text)
	def warn = apply(WARN)
	def warn(text: String) = update(WARN, text)
	def debug = apply(DEBUG)
	def debug(text: String) = update(DEBUG, text)
}

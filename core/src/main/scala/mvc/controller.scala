package org.scalaeye.mvc

import org.scalaeye._, mvc._
import java.lang.reflect._
import scalaj.reflect._

/**
 * 重要的基类。用户须继承该类，才可以定义自己的route。构造函数的参数，表示url的前缀。
 *
 * 有三种方式来声明自己的route规则，如下例：
 *
 * {{{
 * class UsersController extends Controller("/users") {
 *     any("/") = "Hello, index"
 *
 *     def show(id: Int) = {}
 *
 *     @post("/new")
 *     def create() = {}
 * }
 * }}}
 *
 * 定义的优先级为：
 *
 * annotation > method name > any()/get()/post()/...
 *
 * 所有在Controller子类中定义了的route，都将在web server启动时，被自动寻找并处理（因为它继承了Init类）
 *
 */
abstract class Controller(pathPrefix: String = "") extends Init with ScalateRender {

	class DirectAction(action: => Any) extends Action { def perform() = { action } }

	/** 通过直接调用的方式增加route，它们将依次加入到route列表的最后*/
	def any(route: String)(action: => Any) = { Router.append(pathPrefix + route, new DirectAction(action), "any") }
	def get(route: String)(action: => Any) = { Router.append(pathPrefix + route, new DirectAction(action), "get") }
	def post(route: String)(action: => Any) = { Router.append(pathPrefix + route, new DirectAction(action), "post") }
	def put(route: String)(action: => Any) = { Router.append(pathPrefix + route, new DirectAction(action), "put") }
	def delete(route: String)(action: => Any) = { Router.append(pathPrefix + route, new DirectAction(action), "delete") }

	val THIS = this

	/** 该方法将在web server启动时被调用。用于查找所有的public函数及其注解，增加对应的route规则 */
	override def init() {
		val methods = this.getClass.getDeclaredMethods().filter(m => m.getModifiers == Modifier.PUBLIC)
		methods.foreach { m =>
			// 默认是any，函数名作route
			var method = "any"
			var route = "/"+m.getName.toLowerCase

			// 如果有注解，先从注解中取得所需信息
			m.getAnnotations map { anno =>
				anno match {
					case a: any if a.value().length > 0 => route = a.value(); method = "any"
					case a: get if a.value().length > 0 => route = a.value(); method = "get"
					case a: post if a.value().length > 0 => route = a.value(); method = "post"
					case a: put if a.value().length > 0 => route = a.value(); method = "put"
					case a: delete if a.value().length > 0 => route = a.value(); method = "delete"
					case _ =>
				}
			}

			// 由方法定义route具有优先仅，所以使用prepend放在前面
			Router.prepend(pathPrefix + route, new Action() {
				def perform() = {
					m.invoke(THIS, getParamValuesOfMethod(m): _*)
				}
			}, method)
		}
	}

	private def getParamValuesOfMethod(m: Method): Seq[AnyRef] = {
		val nameTypes = getParamNamesTypes(m)
		println("### name types: "+nameTypes)
		val values = getParamNamesTypes(m) map {
			case (name, paramType) =>
				paramType match {
					case "String" => params(name)
					case "Int" => java.lang.Integer.valueOf(params(name).getOrElse("0"))
					case "Short" => java.lang.Short.valueOf(params(name).getOrElse("0"))
					case "Long" => java.lang.Long.valueOf(params(name).getOrElse("0"))
					// case "Char" => java.lang.Character.valueOf(params(name))
					case "Double" => java.lang.Double.valueOf(params(name).getOrElse("0"))
					case "Float" => java.lang.Float.valueOf(params(name).getOrElse("0"))
					case "Boolean" => java.lang.Boolean.valueOf(params(name).getOrElse("0"))

					// case "Byte" => getaram(name).toByte
					// others
					case _ => null
				}
		}
		values
	}

	private def getParamNamesTypes(method: Method): Seq[Tuple2[String, String]] = {
		for {
			clazz <- Mirror.ofClass(method.getDeclaringClass).toSeq
			method <- clazz.allDefs.find(_.name == method.getName).toSeq
			param <- method.flatParams
		} yield (param.name, param.symType.toString)
	}

}
package org.scalaeye.mvc

import org.scalaeye._, mvc._
import java.lang.reflect._

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
abstract class Controller(pathPrefix: String = "") extends Init {

	/** 通过直接调用的方式增加route，它们将依次加入到route列表的最后*/
	def any(route: String)(action: => Any) = { Router.append(pathPrefix + route, action, "any") }
	def get(route: String)(action: => Any) = { Router.append(pathPrefix + route, action, "get") }
	def post(route: String)(action: => Any) = { Router.append(pathPrefix + route, action, "post") }
	def put(route: String)(action: => Any) = { Router.append(pathPrefix + route, action, "put") }
	def delete(route: String)(action: => Any) = { Router.append(pathPrefix + route, action, "delete") }

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
			Router.prepend(pathPrefix + route, m, method)
		}
	}
}

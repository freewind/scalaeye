package org.scalaeye

import org.scalaeye._, mvc._

/** 测试类。定义了几个route，可通过浏览器访问，查看效果 */
class UsersController extends Controller("/users") {

	get("/") {
		println("in users controller")
		<h1>Hello, ScalaEye, in /users/:{ params("a") }</h1>
	}

	def xxx(id: Int) = "Hello, ScalaEye, /users/xxx, id: "+id

	def textCn = "中文测试"
	def plainTextCn = plainText("中文测试")
	def htmlCn = html("中文测试")
	def xmlCn = xml(<a>中文测试</a>)

	def text {
		plainText("Hello, render text")
	}

	def html {
		html("Hello, render html")
	}

	def xml {
		xml(<h1>Hello, render xml</h1>)
	}

	@any("/aaa")
	def abc() {
		println("in /users/aaa")
		plainText("Hello, ScalaEye")
	}
}

package org.scalaeye

import org.scalaeye._, mvc._

/** 测试类。定义了几个route，可通过浏览器访问，查看效果 */
class UsersController extends Controller("/users") {

	get("/") {
		println("in users controller")
		<h1>Hello, ScalaEye, in /users/: {params("a")}</h1>
	}

	/**
	 */
	def xxx(id: Int) {
		"Hello, ScalaEye, /users/xxx, id: "+id
	}

	@any("/aaa")
	def abc() {
		println("in /users/aaa")
		response.setContentType("text/html")
		response.getOutputStream.write("Hello, ScalaEye".getBytes)
		response.getOutputStream.flush()
	}
}

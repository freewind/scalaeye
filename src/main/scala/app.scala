package org.scalaeye

import org.scalaeye._, mvc._

/** 测试类。定义了几个route，可通过浏览器访问，查看效果 */
class UsersController extends Controller("/users") {

	/** 该功能正确实现 */
	get("/") {
		println("in users controller")
		response.getOutputStream.write("Hello, ScalaEye, in /users/".getBytes)
		response.getOutputStream.flush()
	}

	/**
	 * FIXME
	 * 后面两个，可以正确路由，但是函数体却不执行
	 */
	def xxx(id: Int) {

	}

	@any("/aaa")
	def abc() {
		println("in /users/aaa")
		response.setContentType("text/html")
		response.getOutputStream.write("Hello, ScalaEye".getBytes)
		response.getOutputStream.flush()
	}
}

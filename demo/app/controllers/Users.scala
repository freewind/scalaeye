package org.scalaeye

import org.scalaeye._, mvc._
import models._

class Users extends Controller("/users") {

	any("/") {
	}

	def show(id: Long) {
		val user = User.find(id).get
		html("用户名：" + user.name + "，共有" + user.questions.size + "个问题")
	}

	def scalate() = {
		val file = new java.io.File("src/main/webapp/templates/test.jade")
		println(file.getAbsolutePath)
		render(file.getAbsolutePath)
	}

}

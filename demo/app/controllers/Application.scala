package controllers

import org.scalaeye.mvc._
import models._

class Application extends Controller {

	any("/") { 
		<ul>
			<li><a href="/index">使用scalate版首页</a></li>
			<li><a href="/jsp/">jsp view support</a></li>
		</ul>
	}

	any("/index") {
		render("index.jade")
	}

	def initDb = {
		val user = new User
		user.email = "aaa@aaa.com"
		user.name = "Jack"
		user.password = "xxx"
		user.save()

		val q = new Question
		q.title = "第一个问题"
		q.content = "心情很激动"
		q.author = user
		q.save()

		val tag = new Tag
		tag.name = "scala"
		tag.save()

		val answer = new Answer
		answer.question = q
		answer.content = "不知道Ebean行不行"
		answer.save()
	}
}

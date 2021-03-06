package controllers

import org.scalaeye._, mvc._
import models._
import org.fusesource.scalate.support._

class Application extends Controller {

	any("/") {
		<ul>
			<li><a href="/index">使用scalate版首页</a></li>
		</ul>
	}

	any("/scalate") {
		<ul>
			<li>ClassLoader.getSystemClassLoader:{ ClassLoader.getSystemClassLoader }</li>
			<li>ContextLoader:{ Thread.currentThread.getContextClassLoader }</li>
			<li>ScalateCompiler loader:{ classOf[ScalaCompiler].getClassLoader }</li>
		</ul>
	}

	any("/xxx") {
		"XXX"
	}

	any("/yyy") {
		"YYY"
	}

	any("/newsession") {
		session("aaa") = "bbb"
	}

	any("/index") {
		'questions := Question.find().findList()
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

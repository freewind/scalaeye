package controllers

import org.scalaeye._, mvc._
import models._

class Questions extends Controller("/questions") {

	def index = {
		val questions = Question.find().findList()
		html("Get "+questions.size+" questions")
	}

	get("/create") {
		render("create.jade")
	}

	@post
	def create(title: String, content: String) {
		val question = new Question
		question.title = title
		question.content = content
		question.save()
		flash.message("问题发表成功")
		redirect("/questions/show/"+question.id)
	}

	@any("""/show/{id:\d+}""")
	def show(id: Long) {
		'question := Question.find(id).get
		render("show.jade")
	}

}

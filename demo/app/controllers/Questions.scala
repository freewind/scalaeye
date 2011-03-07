package controllers

import org.scalaeye.mvc._
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
		html("title: "+title+", content: "+content)
	}

	def insert() = {
	}
}

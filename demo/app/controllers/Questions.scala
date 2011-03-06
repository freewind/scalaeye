package controllers

import org.scalaeye.mvc._
import models._

class Questions extends Controller("/questions") {

	def index = {
		val questions =  Question.find().findList()
		html("Get " + questions.size + " questions")
	}

	def insert() = {
	}
}

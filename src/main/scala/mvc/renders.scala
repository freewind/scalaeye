package org.scalaeye.mvc

import scala.xml._

trait DefaultRender {

	def renderPlainText(data: AnyRef) { response.asText(); response.write(data.toString) }

	def renderHtml(data: AnyRef) { response.asHtml(); response.write(data.toString) }

	def renderXml(data: AnyRef) { response.asXml(); response.write(data.toString) }

}

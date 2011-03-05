package org.scalaeye.mvc

import scala.xml._

trait DefaultRender {

	def text(text: String) { response.asText(); response.write(text) }

	def html(html: String) { response.asHtml(); response.write(html) }

	def html(xml: Elem) { response.asHtml(); response.write(xml.toString) }

	def xml(xml: Elem) { response.asXml(); response.write(xml.toString) }

}

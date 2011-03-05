package org.scalaeye.mvc

import scala.xml._

trait DefaultRender {

	def plainText(data: AnyRef) { response.asText(); response.write(data.toString) }

	def html(data: AnyRef) { response.asHtml(); response.write(data.toString) }

	def xml(data: AnyRef) { response.asXml(); response.write(data.toString) }

}

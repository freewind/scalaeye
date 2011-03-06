package org.scalaeye.mvc

import scala.xml._

trait DefaultRender {

	def plainText(data: AnyRef) { response.asText(); response.write(data.toString) }

	def html(data: AnyRef) { response.asHtml(); response.write(data.toString) }

	def xml(data: AnyRef) { response.asXml(); response.write(data.toString) }

}

/** 集成scalate */
import org.fusesource.scalate._

package object scalate {
	val engine = new TemplateEngine
}

trait ScalateRender extends DefaultRender {
	def render(path: String) {
		html(scalate.engine.layout(path))
	}
}

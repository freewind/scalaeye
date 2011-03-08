package org.scalaeye.mvc

import scala.xml._
import org.scalaeye._, mvc._

/** 默认的render，直接操作servlet的response，输出内容*/
trait DefaultRender extends MvcContext {

	def plainText(data: AnyRef) { response.asText(); response.write(data.toString) }

	def html(data: AnyRef) { response.asHtml(); response.write(data.toString) }

	def xml(data: AnyRef) { response.asXml(); response.write(data.toString) }

}

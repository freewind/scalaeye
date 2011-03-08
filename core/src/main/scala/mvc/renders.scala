package org.scalaeye.mvc

import scala.xml._
import org.scalaeye._, mvc._

/** 默认的render，直接操作servlet的response，输出内容*/
trait DefaultRender extends MvcContext {

	def plainText(data: AnyRef) { response.asText(); response.write(data.toString) }

	def html(data: AnyRef) { response.asHtml(); response.write(data.toString) }

	def xml(data: AnyRef) { response.asXml(); response.write(data.toString) }

}

/** 集成scalate */
import org.fusesource.scalate._, servlet._

package object scalate {
	object config extends Config {
		def getServletContext = context.servletContext
		def getName = getServletContext.getServletContextName
		def getInitParameterNames = getServletContext.getInitParameterNames
		def getInitParameter(name: String) = getServletContext.getInitParameter(name)
	}
	val engine = new ServletTemplateEngine(config)
	engine.importStatements ::= "import org.scalaeye._, mvc._, dao._;import controllers._; import models._;"

	def createRenderContext: ServletRenderContext = new ServletRenderContext(engine, context.request, context.response, context.servletContext)
}
import scalate._

trait ScalateRender extends DefaultRender {
	def viewBaseDir = Context.webinfDir+"/views"
	def render(path: String, layout: Boolean = true) {
		val renderContext = createRenderContext
		for ((key, value) <- context.copyData) {
			renderContext.attributes(key) = value
		}
		renderContext.include(viewBaseDir + path, layout)
	}
}

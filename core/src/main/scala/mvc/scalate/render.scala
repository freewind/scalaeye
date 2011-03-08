package org.scalaeye.mvc.scalate

import org.fusesource.scalate._, servlet._
import org.scalaeye._, mvc._

trait ScalateRender extends DefaultRender {

	def viewBaseDir = Context.webinfDir / "views"

	def createRenderContext: ServletRenderContext = new ServletRenderContext(engine, context.request, context.response, context.servletContext)

	def render(path: String, layout: Boolean = true) {
		_scalateRenderContext.withValue(createRenderContext) {
			for ((key, value) <- context.copyData) {
				renderContext.attributes(key) = value
			}
			renderContext.include(viewBaseDir + path, layout)
		}
	}
}

package org.scalaeye.mvc

import org.scalaeye._, mvc._
import org.fusesource.scalate._, servlet._
import scala.util.DynamicVariable

package object scalate {

	val _scalateRenderContext = new DynamicVariable[ServletRenderContext](null)
	def renderContext = _scalateRenderContext value

	object ScalateConfig extends Config {
		def getServletContext = context.servletContext
		def getName = getServletContext.getServletContextName
		def getInitParameterNames = getServletContext.getInitParameterNames
		def getInitParameter(name: String) = getServletContext.getInitParameter(name)
	}
	val engine = new ServletTemplateEngine(ScalateConfig)

	val DEFAULT_IMPORTS = engine.importStatements ++ List("import org.scalaeye._, mvc._, dao._", "import controllers._",
		"import models._")

	trait ScalateTagSupport

}

import scalate._

class ScalateInitializer extends Init {

	override def init() {
		engine.importStatements = DEFAULT_IMPORTS
		findSubclassesOf[ScalateTagSupport] foreach { clsname =>
			engine.importStatements :+= "import "+clsname.stripSuffix("$")+"._"
		}
	}
}

package org.scalaeye.mvc

import org.scalaeye._, mvc._, scalate._
import org.fusesource.scalate._, servlet._
import javax.servlet._, http._
import scala.util.DynamicVariable

package object scalate extends MvcContext {

	class ContextData
	val _contextData = new DynamicVariable[ContextData](null)
	def contextData = _contextData value

	val _renderContext = new DynamicVariable[ServletRenderContext](null)
	def renderContext = _renderContext value

	object ScalateConfig extends Config {
		def getServletContext = servletContext
		def getName = getServletContext.getServletContextName
		def getInitParameterNames = getServletContext.getInitParameterNames
		def getInitParameter(name: String) = getServletContext.getInitParameter(name)
	}
	val engine = new ServletTemplateEngine(ScalateConfig)

	val DEFAULT_IMPORTS = engine.importStatements ++ List(
		"import org.scalaeye._, mvc._, dao._",
		"import controllers._",
		"import models._")

	trait ScalateTagSupport

}

object PreDef extends MvcContext with ScalateTagSupport

object ScalateInitializer extends Init {

	def init() = {
		engine.importStatements = DEFAULT_IMPORTS
		findSubclassesOf[ScalateTagSupport] foreach { clsname =>
			engine.importStatements :+= "import "+clsname.stripSuffix("$")+"._"
		}
	}
}

trait ScalateContextSupport extends Handler {

	abstract override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		_contextData.withValue(new ContextData) {
			logger.debug("enter: scalate context data")
			super.handle(req, res, chain);
			logger.debug("leave: scalate context data")
		}
	}

}

/** 用于render scalate文件，将被controller混入 */
trait ScalateRender extends MvcContext {

	/** 默认视图文件的路径前缀 */
	def viewBaseDir = mvc.webinfDir / "views"

	/** 创建一个新的renderContext */
	def createContext = new ServletRenderContext(engine, request, response, servletContext)

	/** 渲染视图 */
	def render(path: String, layout: Boolean = true) {
		val renderContext = createContext
		_renderContext.withValue(renderContext) {
			logger.debug("enter: scalate render context")
			val data = contextData
			for ((key, value) <- context.copyData) {
				renderContext.attributes(key) = value
			}
			renderContext.include(viewBaseDir + path, layout)
			logger.debug("leave: scalate render context")
		}
	}

}

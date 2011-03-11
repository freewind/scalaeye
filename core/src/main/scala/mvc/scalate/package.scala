package org.scalaeye.mvc

import org.scalaeye._, mvc._, scalate._
import org.fusesource.scalate._, servlet._
import javax.servlet._, http._
import scala.util.DynamicVariable
import collection.mutable.ListBuffer

package object scalate extends MvcContext {

	var classesWithTagSupport: List[String] = _

	val _renderContext = new DynamicVariable[ServletRenderContext](null)
	def renderContext = _renderContext value

	object ScalateConfig extends Config {
		def getServletContext = servletContext
		def getName = getServletContext.getServletContextName
		def getInitParameterNames = getServletContext.getInitParameterNames
		def getInitParameter(name: String) = getServletContext.getInitParameter(name)
	}
	val engine = new ServletTemplateEngine(ScalateConfig)
	engine.allowCaching = false
	engine.classLoader = this.getClass.getClassLoader

	logger.info("############################## this.classloader: " + this.getClass.getClassLoader)

	object PreDef extends MvcContext
	engine.importStatements ++= List(
		"import org.scalaeye._, mvc._, dao._",
		"import controllers._",
		"import models._",
		"import scalate.PreDef._")

	val DEFALUT_BINDINGS = List() ++ engine.bindings

	trait ScalateTagSupport

}

object ScalateInitializer extends Init {

	/**
	 * http://scalate.fusesource.org/documentation/scalate-embedding-guide.html#Implicitly_Imported_Bound_Variables
	 */
	def init() = {
		logger.info("Reimport scalate tags")
		val tagClasses = findSubclassesOf[ScalateTagSupport]
		logger.info("Found "+tagClasses.size+" classes with tag support")
		tagClasses foreach { clsname => logger.info("- "+clsname) }

		val tagBindings = ListBuffer[Binding]()
		tagClasses foreach { clsname =>
			tagBindings += Binding("xxx", clsname, true)
		}
		engine.bindings = scalate.DEFALUT_BINDINGS ++ tagBindings

		// 还要将这些tag加入到scalate render context中，将在每个请求发起的时候来做
		// 这里先保存到classesWithTagSupport中
		classesWithTagSupport = tagClasses
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
		logger.info("######################## engine.allowReload: "+engine.allowReload)
		logger.info("######################## engine.allowCaching: "+engine.allowCaching)
		logger.info("######################## engine.classpath: "+engine.classpath)
		val renderContext = createContext
		_renderContext.withValue(renderContext) {
			logger.debug("enter: scalate render context")
			// load tags support
			for (clsname <- classesWithTagSupport) {
				renderContext.attributes("xxx") = getObjectOrCreateInstanceOf[Any](clsname)
			}

			// load context data
			for ((key, value) <- context.copyData) {
				renderContext.attributes(key) = value
			}
			renderContext.include(viewBaseDir + path, layout)
			logger.debug("leave: scalate render context")
		}
	}

}

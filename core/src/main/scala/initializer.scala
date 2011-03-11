package org.scalaeye

import javax.servlet._
import java.io.File
import org.scalaeye._, mvc._

/**
 * 当web server启动时，该类将被自动执行，可进行一些初始化工作
 *
 * 需要在web.xml中有以下配置（一定要放在最前面）
 *
 * {{{
 * <listener>
 * <listener-class>org.scalaeye.Initializer</listener-class>
 * </listener>
 * }}}
 *
 */
class Initializer extends ServletContextListener {

	/** 当前web应用被载入时，该函数将被调用，仅调用一次 */
	def contextInitialized(event: ServletContextEvent) {
		mvc.servletContextEvent = event
		mvc.webappRoot = event.getServletContext.getRealPath("/")
		logger.info("Webapp Root: "+mvc.webappRoot)

		initOnStartup()
	}

	/** 当前web应用被关闭时，该函数将被调用，目前为空 */
	def contextDestroyed(event: ServletContextEvent) {}

	/** 当web应用被载入时执行 */
	private def initOnStartup() = {
		logger.info("Initializing on startup ...")
		val initClasses = findSubclassesOf[Init]
		logger.info("Found "+initClasses.size+" subclasses of "+classOf[Init].getName)
		initClasses foreach { clsname => logger.info("- "+ clsname) }
		initClasses foreach { clsname =>
			getObjectOrCreateInstanceOf[Init](clsname).init()
		}
	}

}

/** 所有继承了该类的类，都将在当前web应用被载入时，被调用并执行init()方法 */
trait Init { def init(): Any }

trait ReloadableOnRequest { def reload(): Any }

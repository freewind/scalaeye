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

	def contextInitialized(event: ServletContextEvent) {
		Context.webappRoot = event.getServletContext.getRealPath("/")
		val it = findSubclassesOf[Init].toList
		it foreach { clsname =>
			getObjectOrCreateInstanceOf[Init](clsname).init()
		}
	}

	def contextDestroyed(event: ServletContextEvent) {
	}

}

/** 所有继承了该类的类，都将在web app启动时，被调用并执行init()方法 */
trait Init { def init(): Any = {} }

package org.scalaeye

import javax.servlet._

/**
 * 当web server启动时，该类将被自动执行，可进行一些初始化工作
 *
 * 需要在web.xml中有以下配置（一定要放在最前面）
 *
 *
 */

class Initializer extends ServletContextListener {

	def contextDestroyed(event: ServletContextEvent) {

	}

	def contextInitialized(event: ServletContextEvent) {

	}

}

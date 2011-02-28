package scalaeye

import scalaeye._, mvc._
import org.eclipse.jetty.servlet.{ ServletContextHandler, ServletHolder }
import org.eclipse.jetty.server.Server

object JettyRunner {
	def run() = {
		println("### start jetty: " + AppConfig.port)
		val server = new Server(AppConfig.port)
		val context = new ServletContextHandler
		context.setContextPath("/")
		server.setHandler(context)
		context.addServlet(new ServletHolder(new DispatcherServlet()), "/*")
		println("### added servlet")
		server.start
		println("### started")
		server.join
	}
}

object App {
	def run(port: Int = 8080) {
		JettyRunner.run()
	}
	def main(args: Array[String]) {
		run()
	}
}

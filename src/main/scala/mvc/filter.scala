package org.scalaeye.mvc

import org.scalaeye._, mvc._
import javax.servlet.{ Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

/**
 * 该类将被当作应用的启动类，定义在web.xml中。它的init方法，被在web app启动时被调用（仅一次）
 * 用于初始化route等所有继承了org.scalaeye.Init的类
 */
// 在WEB-INF/web.xml中，须定义
// <filter>
// 		<filter-name>ScalaEye Filter</filter-name>
// 		<filter-class>org.scalaeye.mvc.WebFilter</filter-class>
// 	</filter>
//
// 	<filter-mapping>
// 		<filter-name>ScalaEye Filter</filter-name>
// 		<url-pattern>/*</url-pattern>
// 		<dispatcher>REQUEST</dispatcher>
// 		<dispatcher>FORWARD</dispatcher>
// 		<dispatcher>INCLUDE</dispatcher>
// 		<dispatcher>ERROR</dispatcher>
// 	</filter-mapping>

class WebFilter extends Filter {

	/** 初始化函数，将在web app启动时被调用一次 */
	def init(filterConfig: FilterConfig) {
		import org.clapper.classutil._
		import java.io._

		// 在用户代码(WEB-INF/classes)中，寻找所有继承了org.scalaeye.Init的类，并执行相应初始化函数
		val classesDir = filterConfig.getServletContext.getRealPath("WEB-INF/classes")
		val finder = ClassFinder(List(new File(classesDir)))
		val classes = finder.getClasses
		val inits = ClassFinder.concreteSubclasses("org.scalaeye.Init", classes)
		inits.foreach { c =>
			Class.forName(c.name).newInstance().asInstanceOf[Init].init()
		}

		// 打印出所有route
		Router.getRouters map { router =>
			println("### router: "+router)
		}
	}

	/** 每一个请求到来时，该方法都将被调用。*/
	def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
		val request = req.asInstanceOf[HttpServletRequest]
		val response = res.asInstanceOf[HttpServletResponse]

		// debug
		printRequest()

		val method = request.getMethod()
		val uri = request.getRequestURI()

		_request.withValue(request) {
			_response.withValue(response) {
				// 寻找匹配的router，并调用其对应的action
				// 即get()/post()等函数最后一个参数体，或controller中的各public方法
				Router.findMatch(method, uri) match {
					case Some(data) => {
						val act = data.router.action
						// FIXME
						// 这里有问题，如果是通过get()/post()等方式定义的，可以被正确调用
						// 但如果对应的是controller中的某方法，则无法调用，怎么办？
						act.perform()
					}
					case _ => println("No router found")
				}
			}
		}
	}

	def destroy() {}

	/** 用于debug */
	private def printRequest() {
		//		println("request.getAutyType()"+request.getAuthType())
		//		println("request.getMethod()"+request.getMethod())
		//		println("request.getContextPath()"+request.getContextPath())
		//		println("request.getCookies()"+request.getCookies())
		//		println("request.getHeader(referer)"+request.getHeader("referer"))
		//		println("request.getPathInfo()"+request.getPathInfo())
		//		println("request.getPathTranslated()"+request.getPathTranslated())
		//		println("request.getQueryString()"+request.getQueryString())
		//		println("request.getRemoteUser()"+request.getRemoteUser())
		//		println("request.getRequestedSessionId()"+request.getRequestedSessionId())
		//		println("request.getRequestURI()"+request.getRequestURI())
		//		println("request.getServletPath()"+request.getServletPath())
	}
}


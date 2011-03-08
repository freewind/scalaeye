package org.scalaeye.mvc

import org.scalaeye._, mvc._
import javax.servlet.{ Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }
import java.io._
import scala.xml.Elem

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

	/** 当该filter被载入时，该函数将被调用 */
	def init(filterConfig: FilterConfig) {
		// 当filterConfig设到全局Context中
		Context.filterConfig = filterConfig
	}

	/** 每一个请求到来时，该方法都将被调用。*/
	def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
		// 如果处于dev模式，重新载入routers（配合jrebel时使用时才需要这样做）
		if (AppConfig.inDev) {
			Routers.reload()
		}

		val request = req.asInstanceOf[HttpServletRequest]
		val response = res.asInstanceOf[HttpServletResponse]

		// 在新Context环境中执行
		Context.execInNew {

			context.request = request
			context.response = response

			// set default encoding(utf8)
			response.setCharacterEncoding(defaultEncoding)

			val method = request.getMethod()
			val uri = request.getRequestURI()

			// 寻找匹配的router，并调用其对应的action
			// 即get()/post()等函数最后一个参数体，或controller中的各public方法
			Routers.findMatch(method, uri) match {
				case Some(data) => {
					context.multiParams = request.getParams() ++ (data.params transform { (k, v) => Seq(v) })

					data.router.action.perform() match {
						case n if n == null => // 忽略
						case _: Unit => // 忽略
						case v => response.asHtml().write(v.toString).flush()
					}
				}
				case _ => println("No router found")
			}

			context.flash.reset
		}
	}

	/** 当filter被关闭时，该函数被调用。暂时为空。*/
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


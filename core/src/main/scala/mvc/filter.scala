package org.scalaeye.mvc

import org.scalaeye._, mvc._, scalate._
import javax.servlet.{ Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }
import java.io._
import scala.xml.Elem
import scala.util.DynamicVariable

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
class WebFilter extends Filter with MvcContext {

	/** 找router并且执行相应操作 */
	class ActionHandler extends Handler {
		override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
			val method = request.getMethod()
			val uri = request.getRequestURI()

			// 寻找匹配的router，并调用其对应的action
			// 即get()/post()等函数最后一个参数体，或controller中的各public方法
			Routers.findMatch(method, uri) match {
				case Some(data) => {
					mvc.multiParams ++= (data.params transform { (k, v) => Seq(v) })
					data.router.action.perform() match {
						case n if n == null => // 忽略
						case _: Unit => // 忽略
						case v => response.asHtml().write(v.toString).flush()
					}
				}
				case _ => println("No router found")
			}

			flash.reset
		}
	}

	/** 真正的处理类，注意后面引入的trait的顺序 */
	class WebHandler extends ActionHandler
		with ScalateContextSupport
		with ParamsSupport
		with ReqResContextSupport
		with ContextSupport
		with PublicDirSupport

	/** 当该filter被载入时，该函数将被调用 */
	def init(filterConfig: FilterConfig) {
		// 当filterConfig设到全局Context中
		mvc.filterConfig = filterConfig
	}

	/** 每一个请求到来时，该方法都将被调用。*/
	def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
		// set default encoding(utf8)
		res.setCharacterEncoding(defaultEncoding)
		new WebHandler().handle(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse], chain)
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

/** 用于生成request和response的context*/
trait ReqResContextSupport extends Handler {
	abstract override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		_request.withValue(req) {
			logger.debug("enter: request context")
			_response.withValue(res) {
				logger.debug("enter: response context")
				super.handle(req, res, chain)
				logger.debug("leave: response context")
			}
			logger.debug("leave: request context")
		}
	}
}

/** 用于处理public文件(直接转给服务器处理) */
trait PublicDirSupport extends Handler {
	abstract override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		val uri = req.getRequestURI()
		if (uri.startsWith(req.getContextPath + AppConfig.app.publicDir)) {
			logger.debug("Found public static request: "+uri)
			chain.doFilter(req, res)
		} else {
			super.handle(req, res, chain)
		}
	}
}

trait ParamsSupport extends Handler {
	abstract override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		_multiParams.withValue(request.getParams()) {
			logger.debug("enter: params context")
			super.handle(req, res, chain)
			logger.debug("leave: params context")
		}
	}
}

trait ContextSupport extends Handler {
	abstract override def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		_context.withValue(new Context) {
			logger.debug("enter: context")
			super.handle(req, res, chain)
			logger.debug("leave: context")
		}
	}
}


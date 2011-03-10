package org.scalaeye

import org.scalaeye._, mvc._
import scala.xml._
import scala.util.DynamicVariable
import scala.collection.JavaConversions._
import javax.servlet._, http._

/**
 * 用于预定义一些常用的操作。
 */
package object mvc {

	// 各操作默认使用的字符集
	val defaultEncoding = "utf8"

	// 用于保存用户通过http提交的数据，可以一key多值
	type MultiParams = MMap[String, Seq[String]]
	val MultiParams = MMap

	/** 定义找到了router时，应该执行的操作 */
	trait Action { def perform(): Any }

	abstract class Handler { def handle(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) }

	// 一些全局属性
	var servletContextEvent: ServletContextEvent = _
	var filterConfig: FilterConfig = _

	/** 用于保存webapp的路径，方便程序中调用 。其值将在web server启动时被注入。*/
	private val WEBAPP_ROOT = "scalaeye.webapp_root"
	var webappRoot: String = _
	def webinfDir: String = webappRoot / "WEB-INF"
	def classesDir: String = webinfDir / "classes"
	def libDir: String = webinfDir / "lib"

	val _request = new DynamicVariable[HttpServletRequest](null)
	val _response = new DynamicVariable[HttpServletResponse](null)
	val _multiParams = new DynamicVariable[MultiParams](null)
	def request = _request value
	def response = _response value
	def multiParams = _multiParams value

	// TODO 以后可能会提取出以下type
	//	type Attributes {
	//		def getAttribute(name:String) : AnyRef
	//		def getAttributeNames(): java.util.Enumeration
	//		def setAttribute(name: String, value: AnyRef)
	//		def removeAttribute(name:String)
	//	}
	//
	//	type Parameters {
	//		def getParameter(name:String):String
	//		def getParameterMap():java.util.Map[_,_]
	//		def getParameterNames(): java.util.Enumeration
	//		def getParameterValues(name:String): Array[String]
	//	}

	/** 常用的隐式转换 */
	implicit def request2rich(request: HttpServletRequest) = new RichRequest(request)
	implicit def response2rich(response: HttpServletResponse) = new RichResponse(response)
	implicit def session2rich(session: HttpSession) = new RichSession(session)
	implicit def cookie2rich(cookie: Cookie) = new RichCookie(cookie)
}

/** 将各context的调用方式集中在一起，可让继承了该trait的类方便操作 */
trait MvcContext {
	def request = mvc.request
	def response = mvc.response
	def multiParams = mvc.multiParams
	def session = request.getSession()
	def cookies = request.getCookies().toList
	def params = new SingleParams { def multiParams = mvc.multiParams }
	def servletContext = servletContextEvent.getServletContext
	def flash: FlashMap = {
		session(FlashMap.SESSION_KEY) match {
			case f: FlashMap => f
			case _ => new FlashMap
		}
	}
}

/** 用于定义一些跟http相关的函数 */
package mvc {

	trait Mime {

		def setContentType(contentType: String): this.type
		def asHtml(): this.type = { setContentType("text/html") }
		def asXml(): this.type = { setContentType("text/xml") }
		def asXhtml(): this.type = { setContentType("aplication/xhtml+xml") }
		def asText(): this.type = { setContentType("text/plain") }
		def asRtf(): this.type = { setContentType("application/rtf") }
		def asWord(): this.type = { setContentType("application/msword") }
		def asExcel(): this.type = { setContentType("application/vnd.ms-excel") }
		def asPpt(): this.type = { setContentType("application/vnd.ms-powerpoint") }
		def asPng(): this.type = { setContentType("image/png") }
		def asGif(): this.type = { setContentType("image/gif") }
		def asJpg(): this.type = { setContentType("image/jpeg") }
		def asPdf(): this.type = { setContentType("application/pdf") }
		def asZip(): this.type = { setContentType("application/zip") }
		def asBinary(): this.type = { setContentType("application/octet-stream") }

	}

}

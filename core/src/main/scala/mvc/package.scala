package org.scalaeye

import org.scalaeye._
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
	type MultiParams = Map[String, Seq[String]]

	/** 定义找到了router时，应该执行的操作 */
	trait Action { def perform(): Any }

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
	implicit def context2mvc(context: Context) = new MvcContextWraper(context)
	implicit def cookie2rich(cookie: Cookie) = new RichCookie(cookie)
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

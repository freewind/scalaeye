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

	/** 常用的隐式转换 */
	implicit def request2rich(request: HttpServletRequest) = new RichRequest(request)
	implicit def response2rich(response: HttpServletResponse) = new RichResponse(response)
	implicit def session2rich(session: HttpSession) = new RichSession(session)
	implicit def context2mvc(context: Context) = new MvcContextWraper(context)
}

/**
 * 给org.scalaeye的Context增加一些功能
 */
package mvc {

	/**
	 * 提供了读取设置request, response, multiParams, params的方法
	 */
	class MvcContextWraper(context: Context) { mvc =>
		private val REQUEST = "scalaeye.mvc.request"
		def request = context.getAs[HttpServletRequest](REQUEST)
		def request_=(request: HttpServletRequest) = context(REQUEST) = request

		private val RESPONSE = "scalaeye.mvc.response"
		def response = context.getAs[HttpServletResponse](RESPONSE)
		def response_=(response: HttpServletResponse) = context(RESPONSE) = response

		private val MULTI_PARAMS = "scalaeye.mvc.multiParams"
		def multiParams = context.getAs[MultiParams](MULTI_PARAMS, Map.empty).withDefaultValue(Seq.empty)
		def multiParams_=(multiParams: MultiParams) = context(MULTI_PARAMS) = multiParams
		def params = new SingleParams { def multiParams = mvc.multiParams }

		private val FILTER_CONFIG = "scalaeye.mvc.filterConfig"
		def filterConfig = context.getAs[FilterConfig](FILTER_CONFIG)
		def filterConfig_=(filterConfig: FilterConfig) = context(FILTER_CONFIG) = filterConfig
		def servletContext = filterConfig.getServletContext
	}

	/** 可让继承了该trait的类，直接使用request, response等方法，而不用加context前缀*/
	trait MvcContext {
		protected def request = context.request
		protected def response = context.response
		protected def multiParams = context.multiParams
		protected def params = context.params
	}

}

/** request, response等的包装类 */
package mvc {

	class RichRequest(request: HttpServletRequest) {

		/** 将request中的各参数变为一个MultiParams*/
		def getParams(): MultiParams = {
			val params = scala.collection.mutable.Map[String, Seq[String]]()
			val names = request.getParameterNames
			while (names.hasMoreElements) {
				val name = names.nextElement.asInstanceOf[String]
				params += (name -> request.getParameterValues(name).toList)
			}
			Map[String, Seq[String]]() ++ params
		}
	}

	class RichResponse(response: HttpServletResponse) extends Mime {
		def setContentType(contentType: String): this.type = { response.setContentType(contentType); this }
		def write(text: String): this.type = { response.getOutputStream.write(text.getBytes(defaultEncoding)); this }
		def write(bytes: Array[Byte]): this.type = { response.getOutputStream.write(bytes); this }
		def flush(): this.type = { response.getOutputStream.flush(); this }
	}

	class RichSession(session: HttpSession)

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

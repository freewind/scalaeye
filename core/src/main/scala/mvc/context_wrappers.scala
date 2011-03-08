package org.scalaeye.mvc

import javax.servlet._, http._
import org.scalaeye._

/** request的包装类 */
class RichRequest(raw: HttpServletRequest) {

	/** 将request中的各参数变为一个MultiParams*/
	def getParams(): MultiParams = {
		val params = scala.collection.mutable.Map[String, Seq[String]]()
		val names = raw.getParameterNames
		while (names.hasMoreElements) {
			val name = names.nextElement.asInstanceOf[String]
			params += (name -> raw.getParameterValues(name).toList)
		}
		Map[String, Seq[String]]() ++ params
	}
}

/** response的包装类 */
class RichResponse(raw: HttpServletResponse) extends Mime {
	def setContentType(contentType: String): this.type = { raw.setContentType(contentType); this }
	def write(text: String): this.type = { raw.getOutputStream.write(text.getBytes(defaultEncoding)); this }
	def write(bytes: Array[Byte]): this.type = { raw.getOutputStream.write(bytes); this }
	def flush(): this.type = { raw.getOutputStream.flush(); this }
	def redirect(uri: String) = raw.sendRedirect(uri)
}

/** session的包装类 */
class RichSession(raw: HttpSession) {
	def apply(key: String) = raw.getAttribute(key)
	def update(key: String, value: String) = raw.setAttribute(key, value)
}

/** cookie的包装类 */
class RichCookie(raw: Cookie) {}

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

	def flash: FlashMap = {
		session(FlashMap.SESSION_KEY) match {
			case f: FlashMap => f
			case _ => new FlashMap
		}
	}

	def session = request.getSession()
	def cookies = request.getCookies().toList
	def servletContext = Context.servletContextEvent.getServletContext

}

/** 可让继承了该trait的类，直接使用request, response等方法，而不用加context前缀*/
trait MvcContext {
	def request = context.request
	def response = context.response
	def session = context.session
	def cookies = context.cookies
	def multiParams = context.multiParams
	def params = context.params
	def flash = context.flash
}

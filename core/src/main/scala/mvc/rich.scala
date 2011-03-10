package org.scalaeye.mvc

import javax.servlet._, http._
import org.scalaeye._

/** request的包装类 */
class RichRequest(raw: HttpServletRequest) {

	/** 将request中的各参数变为一个MultiParams*/
	def getParams(): MultiParams = {
		val params = MultiParams[String, Seq[String]]()
		val names = raw.getParameterNames
		while (names.hasMoreElements) {
			val name = names.nextElement.asInstanceOf[String]
			params += (name -> raw.getParameterValues(name).toList)
		}
		params
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


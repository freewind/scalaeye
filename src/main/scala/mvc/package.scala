package org.scalaeye

import javax.servlet._, http._
import scala.util.DynamicVariable

package object mvc {

	trait Action {
		def perform()
	}

	/** 常用的隐式转换 */
	implicit def request2rich(request: HttpServletRequest) = new RichRequest(request)
	implicit def response2rich(response: HttpServletResponse) = new RichResponse(response)
	implicit def session2rich(session: HttpSession) = new RichSession(session)

	/** 用于保存服务器生成的request和response等对象*/
	val _request = new DynamicVariable[HttpServletRequest](null)
	val _response = new DynamicVariable[HttpServletResponse](null)

	/** 快速取得当前可使用的request和response等对象 */
	def request = _request value
	def response = _response value

}

package mvc {

	class RichRequest(request: HttpServletRequest)
	class RichResponse(response: HttpServletResponse)
	class RichSession(session: HttpSession)

}

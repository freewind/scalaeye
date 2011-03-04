package org.scalaeye

import javax.servlet._, http._
import scala.util.DynamicVariable
import org.scalaeye._
import scala.collection.JavaConversions._

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
	val _params = new DynamicVariable[MultiParams](null)

	/** 快速取得当前可使用的request和response等对象 */
	def request = _request value
	def response = _response value
	def params = _params value
	def param(key: String) = params(key).head

}

package mvc {

	class RichRequest(request: HttpServletRequest) {
		def getParams():  MultiParams = {
			val params = scala.collection.mutable.Map[String, Seq[String]]()
			val names = request.getParameterNames
			while(names.hasMoreElements) {
				val name = names.nextElement.asInstanceOf[String]
				params += (name -> request.getParameterValues(name).toList)
			}
			Map[String, Seq[String]]() ++ params
		}
	}
	class RichResponse(response: HttpServletResponse)
	class RichSession(session: HttpSession)

}

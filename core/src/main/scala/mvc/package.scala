package org.scalaeye

import org.scalaeye._
import scala.xml._
import scala.util.DynamicVariable
import scala.collection.JavaConversions._
import javax.servlet._, http._

package object mvc {

	val defaultEncoding = "utf8"

	type MultiParams = Map[String, Seq[String]]

	/** 常用的隐式转换 */
	implicit def request2rich(request: HttpServletRequest) = new RichRequest(request)
	implicit def response2rich(response: HttpServletResponse) = new RichResponse(response)
	implicit def session2rich(session: HttpSession) = new RichSession(session)

	/** 用于保存服务器生成的request和response等对象*/
	val _request = new DynamicVariable[HttpServletRequest](null)
	val _response = new DynamicVariable[HttpServletResponse](null)
	val _multiParams = new DynamicVariable[MultiParams](Map())
	val _params = new SingleParams {
		def multiParams = _multiParams value
	}

	/** 快速取得当前可使用的request和response等对象 */
	def request = _request value
	def response = _response value
	def multiParams = _multiParams.value.withDefaultValue(Seq.empty)
	def params = _params

}

package mvc {

	/** 所有继承了该类的类，都将在web app启动时，被调用并执行init()方法 */
	trait Init { def init(): Any = {} }

	trait Action {
		def perform(): Any
	}

}

package mvc {

	class RichRequest(request: HttpServletRequest) {
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

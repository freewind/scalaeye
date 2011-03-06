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
	implicit def context2mvc(context: Context) = new MvcContextWraper(context)
}

package mvc {

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

	trait MvcContext {
		protected def request = context.request
		protected def response = context.response
		protected def multiParams = context.multiParams
		protected def params = context.params
	}

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

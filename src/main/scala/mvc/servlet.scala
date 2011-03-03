package org.scalaeye.mvc

import javax.servlet.http.{ HttpServletRequest, HttpServletResponse, HttpServlet }

/** TODO，请忽略该类 */
class DispatcherServlet() extends HttpServlet {
	override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
		val text = request.getPathInfo
		response.getWriter.write(text)
	}
}

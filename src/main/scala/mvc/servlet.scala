package scalaeye.mvc

import javax.servlet.http.{ HttpServletRequest, HttpServletResponse, HttpServlet }

class DispatcherServlet() extends HttpServlet {
	override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
		val text = request.getPathInfo
		response.getWriter.write(text)
	}
}

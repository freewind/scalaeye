package org.scalaeye.mvc.views

import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }
import javax.servlet.ServletException
import org.slf4j._

package object views {
  type modelType = Map[String, Any]
  lazy val logger = LoggerFactory.getLogger(classOf[JSPView])
}

import views._

trait View {
  def contentType: String
  def render(data: AnyRef, request: HttpServletRequest, response: HttpServletResponse): Unit
}

case class JSPView(view: String = null, model: modelType = Map.empty) extends View {
  def contentType: String = "text/html; charset=UTF-8"

  def render(model: AnyRef, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val viewPath = JSPViewPath(view, request)
    logger.debug("viewPath = " + viewPath)
    request.getRequestDispatcher(viewPath) match {
      case null =>
        throw new ServletException("WARN>Could not get RequestDispatcher for [" + viewPath +
          "]: Check that the corresponding file exists within your web application archive!");
      case rd =>
        exposeModelAsRequestAttributes(model, request)
        if (isIncludeRequest(request)) {
          response.setContentType(contentType)
          rd.include(request, response)
        } else {
        	println("forward")
          rd.forward(request, response)
        }
    }
  }

  private def exposeModelAsRequestAttributes(model: AnyRef, request: HttpServletRequest) =
    model match {
      case null =>
      case None =>
      case map if model.isInstanceOf[Map[String, _]] =>
        map.asInstanceOf[Map[String, _]].foreach(it => request.setAttribute(it._1, it._2))
      case _ => request.setAttribute("model", model)
    }

  private def isIncludeRequest(request: HttpServletRequest) =
    request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE) != null

  private val INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri"

  object JSPViewPath {
    def apply(view: String, request: HttpServletRequest): String =
      new JSPViewPath(view, request).viewPath
  }

  trait ViewPath {
    def viewPath: String
  }

  private class JSPViewPath(view: String, request: HttpServletRequest,
    viewPathPrefix: String = "/WEB-INF/views",
    viewFileSuffix: String = ".jsp") extends ViewPath {
    def viewPath = view match {
      case null => requestURI2Path(request) //view 为空, 通过URI自动匹配
      case _ => view2Path
    }

    private def view2Path = view match {
      case v if view.startsWith("/") =>
        viewPathPrefix + view + viewFileSuffix
      case v =>
        viewPathPrefix + "/" + view + viewFileSuffix
    }

    private def requestURI2Path(request: HttpServletRequest): String = {
      val uri = request.getRequestURI
      val path = uri.substring(request.getContextPath.length, uri.length)

      if (path.endsWith("/")) {
        viewPathPrefix + path + "index" + viewFileSuffix
      } else {
        viewPathPrefix + path + viewFileSuffix
      }
    }
  }
}

import org.scalaeye._
import mvc._
trait JSPSupport extends MvcContext{ self => Controller
  def _jsp(view: String = null, model: modelType = Map.empty) ={
  	val REQUEST = "scalaeye.mvc.request"
	val RESPONSE = "scalaeye.mvc.response"
    JSPView(view, model).render(model, 
    	request,
     	response)
 }

  def renderJSP(view: String) = _jsp(view, Map.empty)

  def renderJSP(view: String, model: modelType) = _jsp(view, model)

  def renderJSP(model: modelType) = _jsp(null, model)

  def renderJSP(view: String, model: Tuple2[String, Any]*) = _jsp(view, Map(model: _*))

  def renderJSP(model: Tuple2[String, Any]*) = _jsp(null, Map(model: _*))
}


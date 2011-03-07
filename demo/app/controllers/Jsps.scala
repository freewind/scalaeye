package controllers

import org.scalaeye.mvc._

import views.JSPSupport
class JSPTestController extends Controller("/jsp") with JSPSupport {
  //before {
  //	request.setAttribute("msg", "Hello, World!")
  //}

  get("/") {
    renderJSP()
  }

  get("/users/") {
    renderJSP()
  }

  get("/hello/index") {
    renderJSP()
  }

  get("/hello/hello") {
    renderJSP("msg" -> "hello")
  }

  get("/hello/index1") {
    renderJSP("/jsp/hello/index",
      "user" -> "tomas",
      "msg" -> "hello")
  }

  get("/hello/index2") {
    renderJSP("/jsp/hello/index", "user" -> "tomas", "msg" -> "hello")
  }

  get("/hello/index3") {
    renderJSP("/jsp/hello/index", Map("user" -> "tomas", "nickname" -> "cat"))
  }

  get("/hello/index4") {
    renderJSP(Map("user" -> "tomas")) //"/WEB-INF/views/jsp/hello/index4.jsp  , 404
  }
}

class RootController extends Controller("") {
  get("/favicon.ico") {
    //
  }
}

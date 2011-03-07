<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
--%>
<html>
<head>
<title>jsp view support</title>  
<style type="text/css">
div{
    border: 2px solid blue;
    margin: 5;
    padding: 5;
}
span{
	border:1px solid #000;
	width: 50px;
	color:red;
}
</style>
</head>
<body>
    <h1>JSP View</h1>
    <div>
	链接:
    <%
    	String urls []={
    		"/jsp/users/",
    		"/jsp/hello/hello",
    		"/jsp/hello/index",
    		"/jsp/hello/index1",
    		"/jsp/hello/index2",
    		"/jsp/hello/index3",
    		"/jsp/hello/index4"
    };
    %>
    <ul>
    	<%for(String url : urls){ %>
    		<li><a href="<%=url %>"><%=url %></a>
    	<%} %>
    </ul>
</div>
<div>
代码:
    <pre>
    <code>
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
    </code>
    </pre>
    </div>
</body>
</html>

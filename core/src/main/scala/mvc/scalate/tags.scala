package org.scalaeye.mvc.scalate

import org.scalaeye._, mvc._, scalate._

object PreDef extends MvcContext with ScalateTagSupport

object BuiltInTags extends ScalateTagSupport {

	def in(layoutPath: String) = {
		renderContext.attributes("layout") = "/WEB-INF" / "views" / layoutPath
	}

	def stylesheet(path: String) =
		<link rel="stylesheet" type="text/css" media="screen" href={ AppConfig.app.publicDir+"/stylesheets/"+path+".css" }/>

	def script(path: String) =
		<script src={ AppConfig.app.publicDir+"/javascripts/"+path+".js" } type="text/javascript" charset="utf-8"></script>
}


package org.scalaeye.mvc.scalate

import org.scalaeye._, mvc._, scalate._

/** 内建的scalate tags，可直接在scalate中调用 */
object BuiltInTags extends ScalateTagSupport {

	/** 使用一个layout */
	def in(layoutPath: String) = {
		renderContext.attributes("layout") = "/WEB-INF" / "views" / layoutPath
	}

	/** 输出一个css文件的引用标签 */
	def stylesheet(path: String) =
		<link rel="stylesheet" type="text/css" media="screen" href={ AppConfig.app.publicDir+"/stylesheets/"+path+".css" }/>

	/** 输出一个javascript文件的引用标签 */
	def script(path: String) =
		<script src={ AppConfig.app.publicDir+"/javascripts/"+path+".js" } type="text/javascript" charset="utf-8"></script>
}


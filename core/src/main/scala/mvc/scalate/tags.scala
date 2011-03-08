package org.scalaeye.mvc.scalate

import org.scalaeye._, mvc._, scalate._

object PreDef extends MvcContext with ScalateTagSupport

object BuiltInTags extends ScalateTagSupport {

	def in(layoutPath: String) = {
		renderContext.attributes("layout") = "/WEB-INF" / "views" / layoutPath
	}
}

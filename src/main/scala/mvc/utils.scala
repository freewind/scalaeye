package org.scalaeye.mvc

trait Mime {

	def setContentType(contentType: String): this.type

	def asHtml(): this.type = { setContentType("text/html") }
	def asXml(): this.type = { setContentType("text/xml") }
	def asXhtml(): this.type = { setContentType("aplication/xhtml+xml") }
	def asText(): this.type = { setContentType("text/plain") }
	def asRtf(): this.type = { setContentType("application/rtf") }
	def asWord(): this.type = { setContentType("application/msword") }
	def asExcel(): this.type = { setContentType("application/vnd.ms-excel") }
	def asPpt(): this.type = { setContentType("application/vnd.ms-powerpoint") }
	def asPng(): this.type = { setContentType("image/png") }
	def asGif(): this.type = { setContentType("image/gif") }
	def asJpg(): this.type = { setContentType("image/jpeg") }
	def asPdf(): this.type = { setContentType("application/pdf") }
	def asZip(): this.type = { setContentType("application/zip") }
	def asBinary(): this.type = { setContentType("application/octet-stream") }

}

package org.scalaeye.mvc

trait SingleParams {
	def multiParams: MultiParams

	def get(key: String): Option[String] = {
		multiParams.get(key) match {
			case Some(v) => Some(v.head)
			case _ => None
		}
	}

	def apply(key: String): Option[String] = {
		get(key)
	}
}

package org.scalaeye

import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable

class Context {

	private val data = MuMap[String, Any]()

	def apply(key: String): Option[Any] = {
		data.get(key) match {
			case Some(v) => Some(v)
			case _ => Context.apply(key)
		}
	}

	def update(key: String, value: Any) = data.update(key, value)

	def getAs[T](key: String, defaultValue: T = null) = apply(key).getOrElse(defaultValue).asInstanceOf[T]

	def copyData: Map[String, Any] = Map() ++ data
}

object Context extends Context {

	val _context = new DynamicVariable[Context](this)
	def current = _context value

	def execInNew(block: => Any) = {
		_context.withValue(new Context()) {
			block
		}
	}

}

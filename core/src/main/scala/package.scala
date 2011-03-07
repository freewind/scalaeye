package org

import javax.servlet._, http._
import scala.collection._
import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable
import org.apache.commons.lang._

package object scalaeye {

	def context = Context.current

	implicit def symbol2context(symbol: Symbol) = new SymbolContext(symbol)
	implicit def string2rich(str: String) = new RichString(str)

}

package scalaeye {

	class SymbolContext(symbol: Symbol) {
		def :=(value: Any) {
			context(symbol.name) = value
		}
	}

	class RichString(str: String) {
		def isBlank = StringUtils.isBlank(str)
		def isEmpty = StringUtils.isEmpty(str)
		def whenNotBlank(block: String => Any): Any = if (!str.isBlank) { block(str) }
	}
}

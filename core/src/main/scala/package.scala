package org

import javax.servlet._, http._
import scala.collection._
import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable

package object scalaeye {

	val _context = new DynamicVariable[MuMap[String, Any]](MuMap.empty)
	def ctx = _context value

	implicit def symbol2context(symbol: Symbol) = new SymbolContext(symbol)

}

package scalaeye {

	class SymbolContext(symbol: Symbol) {
		def :=(value: Any) {
			ctx += (symbol.name -> value)
		}
	}
}

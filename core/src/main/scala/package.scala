package org

import javax.servlet._, http._
import scala.collection._
import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable

package object scalaeye {

	def context = Context.current

	implicit def symbol2context(symbol: Symbol) = new SymbolContext(symbol)

}

package scalaeye {

	class SymbolContext(symbol: Symbol) {
		def :=(value: Any) {
			// context += (symbol.name -> value)
		}
	}
}

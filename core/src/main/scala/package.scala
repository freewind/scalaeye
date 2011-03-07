package org

import javax.servlet._, http._
import scala.collection._
import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable
import java.io.File
import org.apache.commons.lang._
import org.clapper.classutil._
import net.lag.logging.Logger

package object scalaeye extends AnyRef with ClassUtils {

	def context = Context.current

	implicit def symbol2context(symbol: Symbol) = new SymbolContext(symbol)
	implicit def string2rich(str: String) = new RichString(str)
	implicit def string2path(str: String) = new PathHelper(str)

	val logger = Logger.get("org.scalaeye")

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
		def ||(str2: String): String = if (!str.isBlank) str else str2
	}

	class PathHelper(str: String) {
		def /(filename: String): String = {
			return str + SystemUtils.FILE_SEPARATOR + filename
		}
	}

}

package scalaeye {

	trait ClassUtils {

		// 搜索WEB-INF/classes下的class文件，lib暂不考虑
		val finder = ClassFinder(List(new File(Context.classesDir)))
		def classes = finder.getClasses

		def findSubclassesOf[T: ClassManifest]: List[String] = {
			val clsname = classManifest[T].erasure.getName
			ClassFinder.concreteSubclasses(clsname, classes) map { _.name } toList
		}

		def getObjectOrCreateInstanceOf[T](clsname: String): T = {
			if (clsname.endsWith("$")) { // it's an object
				val clazz = Class.forName(clsname)
				clazz.getField("MODULE$").get(clazz).asInstanceOf[T]
			} else { // class
				Class.forName(clsname).newInstance().asInstanceOf[T]
			}
		}

	}

}

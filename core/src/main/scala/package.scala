package org

import javax.servlet._, http._
import scala.util.DynamicVariable
import java.io.File
import org.apache.commons.lang._
import org.clapper.classutil._
import net.lag.logging.Logger

/** 定义一些基础的经常用到的方法或隐式转换 */
package object scalaeye extends ClassUtils with ClassAliases {

	/** 快速得到当前context */
	def context = Context.current

	/** 全局logger*/
	val logger = Logger.get("org.scalaeye")

	/** 常用的重要隐式转换 */
	implicit def symbol2context(symbol: Symbol) = new SymbolContext(symbol)
	implicit def string2rich(str: String) = new RichString(str)
	implicit def string2path(str: String) = new PathHelper(str)

	/** 当web应用被载入时执行 */
	def initOnStartup() = {
		findSubclassesOf[Init] foreach { clsname =>
			getObjectOrCreateInstanceOf[Init](clsname).init()
		}
	}

	/** 在dev模式下，当每个新的请求到来时执行 */
	def reloadOnRequest() = {
		findSubclassesOf[ReloadableOnRequest] foreach { clsname =>
			getObjectOrCreateInstanceOf[ReloadableOnRequest](clsname).reload()
		}
	}

}

/** 定义一些将被隐式转换的类 */
package scalaeye {

	/** 为symbol提供一些功能，主要是为了简化与context的交互。*/
	class SymbolContext(symbol: Symbol) {
		/**
		 * context("key") = value 现在可写成 'key = value
		 */
		def :=(value: Any) {
			context(symbol.name) = value
		}
	}

	/** 为String提供一些常用操作 */
	class RichString(str: String) {
		/** 为null或空白 */
		def isBlank = StringUtils.isBlank(str)

		/** 长度为0 */
		def isEmpty = StringUtils.isEmpty(str)

		/** 如果不为空，则对值进行什么操作*/
		def whenNotBlank(block: String => Any): Any = if (!str.isBlank) { block(str) }

		/**
		 * 如果第一个不为空，则使用第一个，否则使用第二个
		 * 例如：mode = System.getProperty("mode") || "dev"
		 */
		def ||(str2: String): String = if (!str.isBlank) str else str2
	}

	/** 提供一些简化的路径操作*/
	class PathHelper(str: String) {
		/**
		 * 简化路径合并。"WEB-INF" / "lib" 相当于 "WEB-INF" + FILE_SEPARATOR + "lib"
		 */
		def /(filename: String): String = {
			return str + SystemUtils.FILE_SEPARATOR + filename
		}
	}

}

/** 定义一些被org.scalaeye混入的工具函数 */
package scalaeye {

	/** 用于处理与类相关的操作，感谢classutil项目(https://github.com/bmc/classutil) */
	trait ClassUtils {

		// 搜索WEB-INF/classes下的class文件，lib暂不考虑（太慢）
		val finder = ClassFinder(List(new File(Context.classesDir)))
		def classes = finder.getClasses

		/** 得到某个类的子类 */
		def findSubclassesOf[T: ClassManifest]: List[String] = {
			val clsname = classManifest[T].erasure.getName
			ClassFinder.concreteSubclasses(clsname, classes) map { _.name } toList
		}

		/** 如果传入的类名对应的是一个object，则找到该object；如果是一个普通的类，则生成一个新实例 */
		def getObjectOrCreateInstanceOf[T](clsname: String): T = {
			if (clsname.endsWith("$")) { // it's an object
				val clazz = Class.forName(clsname)
				clazz.getField("MODULE$").get(clazz).asInstanceOf[T]
			} else { // classpa
				Class.forName(clsname).newInstance().asInstanceOf[T]
			}
		}

	}

	trait ClassAliases {
		type JInteger = java.lang.Integer
		type JLong = java.lang.Long
		type JByte = java.lang.Byte
		type JList[T] = java.util.List[T]
		type JSet[T] = java.util.Set[T]
		type JMap[K, V] = java.util.Map[K, V]
	}

}

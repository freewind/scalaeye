package org.scalaeye

import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable

/**
 * 重要的类。本质上是一个Map，用于读取属性数据。对于每一个新的http请求，都将会生成一个新的Context供使用。
 */
class Context {

	/** 用于保存数据的可变Map，仅内部使用 */
	private val data = MuMap[String, Any]()

	/** 取值，先从自己找，找不到再从全局Context中找。可简写成context(key)。*/
	def apply(key: String): Option[Any] = {
		data.get(key) match {
			case Some(v) => Some(v)
			case _ => Context.apply(key)
		}
	}

	/** 更新属性，仅更新自己 。可简写成context(key)=value */
	def update(key: String, value: Any) = data.update(key, value)

	/** 读取某属性的值，并转换为特定的类型，可设置默认值。 */
	def getAs[T](key: String, defaultValue: T = null): T = {
		apply(key).getOrElse(defaultValue).asInstanceOf[T]
	}

	/** 得到自己和全局Context的数据的copy，返回值为不可变的Map */
	def copyData: Map[String, Any] = Map() ++ data ++ Context.copyData
}

/** 全局Context，可用于保存一些全局使用的，不会失效的数据。 */
object Context extends Context {

	/** 使用DynamicVariable这个强大的线程安全的容器，默认值为全局Context */
	val _context = new DynamicVariable[Context](this)

	/** 得到容器中的当前context */
	def current = _context value

	/** 生成一个新的context供使用。在该函数内，使用Context.current将得到这个新context */
	def execInNew(block: => Any) = {
		_context.withValue(new Context()) { block }
	}

	/** 用于保存webapp的路径，方便程序中调用 。其值将在web server启动时被注入。*/
	private val WEBAPP_ROOT = "org.scalaeye.webapp_root"
	def webappRoot: String = getAs[String](WEBAPP_ROOT)
	def webappRoot_=(path: String) = update(WEBAPP_ROOT, path)
	def classesDir: String = webappRoot / "WEB-INF" / "classes"
	def libDir: String = webappRoot / "WEB-INF" / "lib"

}


package org.scalaeye

import scala.collection.mutable.{ Map => MuMap }
import scala.util.DynamicVariable
import javax.servlet.{ FilterConfig, ServletContextEvent }

/**
 * 重要的类。本质上是一个Map，用于读取属性数据。对于每一个新的http请求，都将会生成一个新的Context供使用。
 */
class Context {

	/** 用于保存数据的可变Map，仅内部使用 */
	private val data = MuMap[String, Any]()

	/** 取值，可简写成context(key)。*/
	def apply(key: String): Option[Any] = data.get(key)

	/** 更新属性，仅更新自己 。可简写成context(key)=value */
	def update(key: String, value: Any) = data.update(key, value)

	/** 读取某属性的值，并转换为特定的类型，可设置默认值。 */
	def getAs[T](key: String, defaultValue: T = null): T = {
		apply(key).getOrElse(defaultValue).asInstanceOf[T]
	}

	/** 得到数据的copy，返回值为不可变的Map */
	def copyData: Map[String, Any] = Map() ++ data

}


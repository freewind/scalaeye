package org.scalaeye.mvc

import javax.servlet._, http._
import org.scalaeye._, mvc._
import scala.util.DynamicVariable

/** 该trait提供了一些函数，只操作MultiParams中的第一个值 */
trait SingleParams {

	/** 对应的multiParams*/
	def multiParams: MultiParams

	/** 得到某属性的值。不论有多少个，只使用第一个 */
	def get(key: String): Option[String] = {
		multiParams.get(key) match {
			case Some(v) => Some(v.head)
			case _ => None
		}
	}

	/** 可通过()来取值 */
	def apply(key: String): Option[String] = {
		get(key)
	}
}

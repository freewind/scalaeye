package org.scalaeye.dao

import com.avaje.ebean._

/**
 * 用于方便得到Ebean的server对象，该对象为线程安全的，可用于执行各种数据库操作
 */
trait EbeanServer {
	val ebean = Ebean.getServer(null)
}

/**
 * 供model继承的类，提供了增删改等操作
 */
abstract class EbeanEntity extends EbeanServer {

	def save(): Unit = ebean.save(this);

	def delete(): Unit = ebean.delete(this);

}

/**
 * 供dao继承的类，提供了一些常用的查询操作
 */
abstract class EbeanDao[T] extends EbeanServer {

	type M[T] = Manifest[T]

	/** 使用id来查询 */
	def find(id: Any)(implicit m: M[T]): Option[T] = {
		ebean.find(m.erasure, id) match {
			case x: AnyRef => Some(x.asInstanceOf[T])
			case _ => None
		}
	}

	/** 得到一个query对象*/
	def find()(implicit m: M[T]): Query[T] = ebean.find(m.erasure).asInstanceOf[Query[T]]

	/** TODO 抄来的函数，还不理解，先去掉 */
	//	def ref(id: Any)(implicit m: M[T]): Option[T] = {
	//		ebean.getReference(m.erasure, id) match {
	//			case x: AnyRef => Some(x.asInstanceOf[T])
	//			case _ => None
	//		}
	//	}

}

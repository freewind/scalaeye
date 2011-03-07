package org.scalaeye.dao

import com.avaje.ebean._

trait EbeanServer {
	def ebean = Ebean.getServer(null)
}

abstract class EbeanEntity extends EbeanServer {

	def save(): Unit = ebean.save(this);

	def delete(): Unit = ebean.delete(this);

}

abstract class EbeanDao[T] extends EbeanServer {

	type M[T] = Manifest[T]

	def find(id: Any)(implicit m: M[T]): Option[T] = {
		ebean.find(m.erasure, id) match {
			case x: AnyRef => Some(x.asInstanceOf[T])
			case _ => None
		}
	}

	def find()(implicit m: M[T]): Query[T] = ebean.find(m.erasure).asInstanceOf[Query[T]]

	def ref(id: Any)(implicit m: M[T]): Option[T] = {
		ebean.getReference(m.erasure, id) match {
			case x: AnyRef => Some(x.asInstanceOf[T])
			case _ => None
		}
	}

}

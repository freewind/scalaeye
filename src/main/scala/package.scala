package org

import javax.servlet._, http._
import scala.collection._

package object scalaeye {

	type MultiParams = Map[String, Seq[String]]

}

package scalaeye {

	/** 所有继承了该类的类，都将在web app启动时，被调用并执行init()方法 */
	trait Init {
		def init(): Any = {}
	}

}

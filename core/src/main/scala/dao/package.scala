package org.scalaeye

/**
 * 预定义了一些在dao中常用的类型
 */
package object dao {

	// 提供以下这些类，主要是因为如果使用了java中的orm，如Ebean,hibernate,jpa之类，
	// 在定义时必须使用java中的集合类
	type JInteger = java.lang.Integer
	type JLong = java.lang.Long
	type JDouble = java.lang.Double
	type JList[T] = java.util.List[T]
	type JSet[T] = java.util.Set[T]
	type JMap[K, V] = java.util.Map[K, V]

}

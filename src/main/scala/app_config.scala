package org.scalaeye

abstract class Mode
case object dev extends Mode
case object prod extends Mode
case object test extends Mode

/** 程序配置类，TODO，请忽略 */
object AppConfig {

	var port = 8080
	var mode: Mode = dev

}

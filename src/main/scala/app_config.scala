package scalaeye

abstract class Mode
case object dev extends Mode
case object prod extends Mode
case object test extends Mode

object AppConfig {

	var port = 8080
	var mode: Mode = dev

}

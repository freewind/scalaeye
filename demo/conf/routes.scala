import models._

object routes {

	get("/") -> App.index
	any("/:controller/:action/?") -> "controller.action"

}

package scalaeye.mvc

import scalaeye._, mvc._
import org.scalatest._, matchers._
import xml._
import java.text._

class RouterTest extends FunSuite with ShouldMatchers {

	test("paramNames") {
		var router = new Router("/articles/{year}/{month}/{day}")
		router.paramNames should equal(List("year", "month", "day"))

		router = new Router("""/articles/{id:\d+}/a-{title}/*""")
		router.paramNames should equal(List("id", "title"))
	}

	test("paramNames with spaces") {
		var router = new Router("/articles/{ year}/{month }/{ day }")
		router.paramNames should equal(List("year", "month", "day"))

		router = new Router("""/articles/{ id : \d+ }/a-{ title }/*""")
		router.paramNames should equal(List("id", "title"))
	}

	test("{var}") {
		val router = Router("/users/{id}")
		router.parse("/users/123") should equal(true, Map("id" -> "123"))
		router.parse("/users/abc") should equal(true, Map("id" -> "abc"))
		router.parse("/users/123/") should equal(true, Map("id" -> "123"))
	}

	test("f{var}x") {
		val router = Router("/users/f{id}x")
		router.parse("/users/f123x") should equal(true, Map("id" -> "123"))
		router.parse("/users/fabcx") should equal(true, Map("id" -> "abc"))
		router.parse("/users/f123x/") should equal(true, Map("id" -> "123"))

		// {id}不能为空
		router.parse("/users/fx") should equal(false, Map())
	}

	test("{var} failed") {
		val router = Router("/users/{id}")
		router.parse("/userssss/123") should equal(false, Map())
		router.parse("/users/123/456") should equal(false, Map())
		router.parse("/users/") should equal(false, Map())
	}

	test("{regex}") {
		val router = Router("""/users/{id:\d+}""")
		router.parse("/users/123") should equal(true, Map("id" -> "123"))
		router.parse("/users/123/") should equal(true, Map("id" -> "123"))
	}

	test("0{regex}0") {
		val router = Router("""/users/0{id:\d+}0""")
		router.parse("/users/01230") should equal(true, Map("id" -> "123"))
		router.parse("/users/01230/") should equal(true, Map("id" -> "123"))

		// id不能为空
		router.parse("/users/00/") should equal(false, Map())
	}

	test("{regex} 2") {
		val router = Router("""/users/0{id:\d*}0""")
		// 即使正则中用了*（可为空），使用{}括起来的，也不能为空
		router.parse("/users/00") should equal(false, Map())
	}

	test("{regex} 3") {
		val router = Router("""/users/{id:\d+}""")
		router.parse("/users/abc") should equal(false, Map())
		router.parse("/users/a123") should equal(false, Map())
	}

	test("tail /*") {
		val router = Router("/users/*")
		router.parse("/users/123/456") should equal(true, Map())
		router.parse("/users/123/") should equal(true, Map())
		router.parse("/users/123") should equal(true, Map())
		router.parse("/users/") should equal(true, Map())
		router.parse("/users") should equal(true, Map())
	}

	test("{var} and tail /*") {
		val router = Router("/users/{id}/*")
		router.parse("/users/123/456") should equal(true, Map("id" -> "123"))
		router.parse("/users/123/") should equal(true, Map("id" -> "123"))
		router.parse("/users/123") should equal(true, Map("id" -> "123"))
		router.parse("/users/") should equal(false, Map())
		router.parse("/users") should equal(false, Map())
	}

	test("{file}.{ext}") {
		val router = Router("/downloads/{file}.{ext}")
		router.parse("/downloads/aaa.gif") should equal(true, Map("file" -> "aaa", "ext" -> "gif"))
	}
}

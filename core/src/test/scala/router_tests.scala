package org.scalaeye.mvc

import org.scalaeye._, mvc._
import org.scalatest._, matchers._
import xml._
import java.text._

/** router相关的测试类 */
class RouterTest extends FunSuite with ShouldMatchers {

	private val action = () => ""

	private def createRouter(pattern: String) = new Router(pattern, new Action() { def perform() = {} })

	test("paramNames") {
		var router = createRouter("/articles/{year}/{month}/{day}")
		router.paramNames should equal(List("year", "month", "day"))

		router = createRouter("""/articles/{id:\d+}/a-{title}/*""")
		router.paramNames should equal(List("id", "title"))
	}

	test("paramNames with spaces") {
		var router = createRouter("/articles/{ year}/{month }/{ day }")
		router.paramNames should equal(List("year", "month", "day"))

		router = createRouter("""/articles/{ id : \d+ }/a-{ title }/*""")
		router.paramNames should equal(List("id", "title"))
	}

	test("{var}") {
		val router = createRouter("/users/{id}")
		router.parse("/users/123") should equal(Some(Map("id" -> "123")))
		router.parse("/users/abc") should equal(Some(Map("id" -> "abc")))
		router.parse("/users/123/") should equal(Some(Map("id" -> "123")))
	}

	test("f{var}x") {
		val router = createRouter("/users/f{id}x")
		router.parse("/users/f123x") should equal(Some(Map("id" -> "123")))
		router.parse("/users/fabcx") should equal(Some(Map("id" -> "abc")))
		router.parse("/users/f123x/") should equal(Some(Map("id" -> "123")))

		// {id}不能为空
		router.parse("/users/fx") should equal(None)
	}

	test("{var} failed") {
		val router = createRouter("/users/{id}")
		router.parse("/userssss/123") should equal(None)
		router.parse("/users/123/456") should equal(None)
		router.parse("/users/") should equal(None)
	}

	test("{regex}") {
		val router = createRouter("""/users/{id:\d+}""")
		router.parse("/users/123") should equal(Some(Map("id" -> "123")))
		router.parse("/users/123/") should equal(Some(Map("id" -> "123")))
	}

	test("0{regex}0") {
		val router = createRouter("""/users/0{id:\d+}0""")
		router.parse("/users/01230") should equal(Some(Map("id" -> "123")))
		router.parse("/users/01230/") should equal(Some(Map("id" -> "123")))

		// id不能为空
		router.parse("/users/00/") should equal(None)
	}

	test("{regex} 2") {
		val router = createRouter("""/users/0{id:\d*}0""")
		// 即使正则中用了*（可为空），使用{}括起来的，也不能为空
		router.parse("/users/00") should equal(None)
	}

	test("{regex} 3") {
		val router = createRouter("""/users/{id:\d+}""")
		router.parse("/users/abc") should equal(None)
		router.parse("/users/a123") should equal(None)
	}

	test("tail /*") {
		val router = createRouter("/users/*")
		router.parse("/users/123/456") should equal(Some(Map()))
		router.parse("/users/123/") should equal(Some(Map()))
		router.parse("/users/123") should equal(Some(Map()))
		router.parse("/users/") should equal(Some(Map()))
		router.parse("/users") should equal(Some(Map()))
	}

	test("{var} and tail /*") {
		val router = createRouter("/users/{id}/*")
		router.parse("/users/123/456") should equal(Some(Map("id" -> "123")))
		router.parse("/users/123/") should equal(Some(Map("id" -> "123")))
		router.parse("/users/123") should equal(Some(Map("id" -> "123")))
		router.parse("/users/") should equal(None)
		router.parse("/users") should equal(None)
	}

	test("{file}.{ext}") {
		val router = createRouter("/downloads/{file}.{ext}")
		router.parse("/downloads/aaa.gif") should equal(Some(Map("file" -> "aaa", "ext" -> "gif")))
	}
}

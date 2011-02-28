package scalaeye.mvc

import java.util.regex.Pattern
import scala.util.matching.Regex, Regex.Match
import scala.collection._

class Router(pattern: String) {

	private val varPattern = """\{([^/]+?)\}""".r

	val paramNames: List[String] = {
		varPattern.findAllIn(pattern).matchData.map(m => {
			val varName = m.group(1).trim
			if (varName.contains(":")) {
				varName substring (0, varName.indexOf(':')) trim
			} else varName
		}).toList
	}

	val regexString: String = {
		var x = (pattern.stripSuffix("/").split("[{}]").zipWithIndex collect {
			case (item, i) => {
				if (i % 2 == 0) {
					"""\Q"""+item+"""\E"""
				} else {
					"{"+item+"}"
				}
			}
		}).mkString

		if (x.endsWith("""/*\E""")) {
			x = x.stripSuffix("""/*\E""")+"""\E/?.*"""
		}

		x = "^"+x.replace("""\Q\E""", "")+"$"

		val r = varPattern.replaceAllIn(x, m => {
			val varName = m.group(1).trim
			if (varName.contains(":")) {
				val re: String = varName.substring(varName.indexOf(':') + 1).trim()
				"("+re.replace("""\""", """\\""")+")"
			} else "([^/]+)"
		}).replaceAll("/[*]$", "/?.*?")
		println(">>>>>: "+r)
		r
	}

	val regexPattern: Regex = {
		new Regex(regexString, paramNames: _*)
	}

	def parse(url: String): Tuple2[Boolean, Map[String, String]] = {
		regexPattern.findFirstMatchIn(url.stripSuffix("/")) match {
			case Some(m) => {
				val list = paramNames map (name => name -> m.group(name))
				if (list exists (_._2 == "")) (false, Map())
				else (true, Map(list: _*))
			}
			case _ => (false, Map())
		}
	}

}

object Router {
	def apply(pattern: String) = new Router(pattern)
}

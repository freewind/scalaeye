import sbt._

class ScalaeyeProject(info: ProjectInfo) extends DefaultWebProject(info) {

	lazy val demo = project("demo", "Demo of ScalaEye")

	override val jettyPort = 8080

	override def libraryDependencies = Set(
		"org.slf4j" % "slf4j-api" % "1.6.1" % "compile->default",
		"org.slf4j" % "slf4j-nop" % "1.6.1" % "compile->default",

		"commons-fileupload" % "commons-fileupload" % "1.2.2" % "compile->default",
		"commons-lang" % "commons-lang" % "2.6" % "compile->default",
		"commons-io" % "commons-io" % "2.0.1" % "compile->default",
		// "commons-codec" % "commons-codec" % "1.4" % "compile->default",
		// "commons-collections" % "commons-collections" % "3.2.1" % "compile->default",

		"org.mortbay.jetty" % "jetty" % "6.1.25" % "compile->default", // 用于编译
		"org.mortbay.jetty" % "jetty" % "6.1.25" % "test->default", // 用于sbt的jetty-run
		"org.scalatest" % "scalatest" % "1.2" % "test->default",
		"junit" % "junit" % "4.5" % "test->default",

		// scalaj-reflect用来取得函数的参数名（仓库里还没有）
		// "org.scalaj" %% "scalaj-reflect" % "0.1-SNAPSHOT" % "compile",
		"org.scala-lang" % "scalap" % "2.8.1" withSources(),
		"org.scala-tools.testing" %% "specs" % "1.6.6",

		"postgresql" % "postgresql" % "9.0-801.jdbc4" % "compile->default",
		// "p6spy" % "p6spy" % "1.3" % "test->default"

		"org.clapper" %% "classutil" % "0.3.4" % "compile->default" //
		) ++ super.libraryDependencies

	// to work with jrebel
	// override def scanDirectories = Nil

	// 解决编译java文件时，未使用utf8字符集导致中文乱码的问题("-encoding", "utf8")
	override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-encoding", "utf8")
	// override def compileOptions = super.compileOptions ++ compileOptions("-g")

}

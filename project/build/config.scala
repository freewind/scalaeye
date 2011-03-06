import sbt._

class ScalaeyeProject(info: ProjectInfo) extends ParentProject(info) {

	lazy val core = project("core", "core", new CoreProject(_))

	class CoreProject(info: ProjectInfo) extends DefaultProject(info) {
		// 解决编译java文件时，未使用utf8字符集导致中文乱码的问题
		override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-encoding", "utf8")

		val slf4j_api = "org.slf4j" % "slf4j-api" % "1.6.1" % "compile->default"
		val slf4j_nop = "org.slf4j" % "slf4j-nop" % "1.6.1" % "compile->default"

		val fileupload = "commons-fileupload" % "commons-fileupload" % "1.2.2" % "compile->default"
		val commons_lang = "commons-lang" % "commons-lang" % "2.6" % "compile->default"
		val commons_io = "commons-io" % "commons-io" % "2.0.1" % "compile->default"
		// "commons-codec" % "commons-codec" % "1.4" % "compile->default",
		// "commons-collections" % "commons-collections" % "3.2.1" % "compile->default",

		val scalate = "org.fusesource.scalate" % "scalate-core" % "1.4.1" % "compile"

		val cglib = "cglib" % "cglib-nodep" % "2.2"
		val ebean = "org.avaje" % "ebean" % "2.7.2" % "compile"
		val classutil = "org.clapper" %% "classutil" % "0.3.4" % "compile->default" //

		// scalaj-reflect用来取得函数的参数名（仓库里还没有）
		// "org.scalaj" %% "scalaj-reflect" % "0.1-SNAPSHOT" % "compile",
		val scala_lang = "org.scala-lang" % "scalap" % "2.8.1" withSources ()

		// jdbc drivers
		val h2 = "com.h2database" % "h2" % "1.2.127" % "provided"
		val postgresql = "postgresql" % "postgresql" % "9.0-801.jdbc4" % "compile->default"
		val mysqlDriver = "mysql" % "mysql-connector-java" % "5.1.10" % "provided"
		val msSqlDriver = "net.sourceforge.jtds" % "jtds" % "1.2.4" % "provided"
		val derbyDriver = "org.apache.derby" % "derby" % "10.7.1.1" % "provided"
		// "p6spy" % "p6spy" % "1.3" % "test->default"

		val jetty_compile = "org.mortbay.jetty" % "jetty" % "6.1.25" % "compile->default" // 用于编译
		val jetty_test = "org.mortbay.jetty" % "jetty" % "6.1.25" % "test->default" // 用于sbt的jetty-run
		val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test->default"
		val junit = "junit" % "junit" % "4.5" % "test->default"
	}

	lazy val demo = project("demo", "demo", new DemoProject(_), core)
	class DemoProject(info: ProjectInfo) extends DefaultWebProject(info) with ScalaEyeStandardProject {
		// 解决编译java文件时，未使用utf8字符集导致中文乱码的问题
		override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-encoding", "utf8")

		override val jettyPort = 8080
	}

	trait ScalaEyeStandardProject extends DefaultWebProject {
		override def mainScalaSourcePath = "app"
		override def mainResourcesPath = "conf"
		override def testScalaSourcePath = "test"
		override def testResourcesPath = "test_files"
		override def dependencyPath = "lib"
		override def managedDependencyPath = "lib_managed"
		override def webappPath = "webapp"
		override def mainJavaSourcePath = mainScalaSourcePath
		override def testJavaSourcePath = testScalaSourcePath

		// 监视app/views
		val viewsDir = mainScalaSourcePath / "views"
		override def watchPaths = super.watchPaths +++ (viewsDir ** "*")

		private def copyViews = task { FileUtilities.sync(viewsDir, webappPath / "WEB-INF" / "views", log) } describedAs BasicScalaProject.CopyResourcesDescription

		override def prepareWebappAction = super.prepareWebappAction.dependsOn(copyViews)
	}

}

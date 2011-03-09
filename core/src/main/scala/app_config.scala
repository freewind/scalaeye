package org.scalaeye

import net.lag.configgy._
import net.lag.logging.Logger
import org.scalaeye._

/**
 * 该类用于导入配置文件。
 *
 * 配置文件位于conf目录下，提倡为不同的环境使用不同的配置文件，默认提供了三个：dev.conf, test.conf, prod.conf
 *
 * 格式使用[configgy](https://github.com/robey/configgy)规定的格式，简单来讲就是嵌套，比较好懂。
 *
 * 类中有一个词叫mode，需要简单讲一下。它其实就是程序所使用的配置文件名的前缀，例如你使用了dev.conf，则mode就为dev，
 * 使用了abc.conf，mode即为abc。程序中预定义了三个mode名，分别是dev、test、prod，在程序中有特别意义,其它的可自定义。
 *
 * 如果想让程序使用不同的配置文件，可通过System.setProperty("mode", new_mode)来设置。在sbt中比较简单，这样操作：
 *
 * > sbt
 * > set mode dev
 * > jetty-run
 *
 * 则程序将以dev方式启动（dev也是默认方式）。如果想换成其它方式，可：
 *
 * > set mode prod
 * > jetty-restart
 *
 * 因为该类继承了Init，所以当web应用被载入的时候，其init函数将被执行，导入指定的配置文件。配置文件中，预定义了一些内容：
 * app, db, log, plugins, cache, mail
 *
 * 我们可以使用这样的方式去取值：
 *
 * {{{
 * AppConfig.app.getString("name") // 得到 app.name的值
 * AppConfig.db.getInt("port") // 得到db.port的值
 * AppConfig.getRoot.getString("abc") // 得到abc的值
 * AppConfig.get("aaa").getBoolean("bbb") // 得到aaa.bbb的值
 * }}}
 *
 */
object AppConfig extends Init with ReloadableOnRequest {

	// 默认使用的配置文件名，使用系统属性mode或dev
	private var _mode: String = System.getProperty("mode") || "dev"

	// 用于持有configgy读取数据后产生的对象
	private var config: Config = _

	/** 重新导入配置文件 */
	def load(mode: String = this.mode) {
		this._mode = mode
		Configgy.configure(Context.classesDir / (mode+".conf"))
		this.config = Configgy.config
	}

	/** 得到当前mode */
	def mode = _mode

	/** 当web应用被载入时，该函数将被调用 */
	def init() = reload()

	def reload() {
		this.load()
	}

	/** 得到configgy产生的对象 */
	def getRoot: Config = config
	/** 得到某一块内容 */
	def get(key: String): Option[ConfigMap] = config.getConfigMap(key)

	/** 得到预定义的各块 */
	//	def app: ConfigMap = { config.configMap("app") }
	//	def db: ConfigMap = { config.configMap("db") }
	//	def log: ConfigMap = { config.configMap("log") }
	//	def plugins: ConfigMap = { config.configMap("plugins") }
	//	def cache: ConfigMap = { config.configMap("cache") }
	//	def mail: ConfigMap = { config.configMap("mail") }

	/** 当前是否处于dev或test或prod模式*/
	def inDev: Boolean = this.mode == "dev"
	def inTest: Boolean = this.mode == "test"
	def inProd: Boolean = this.mode == "prod"

	object app {
		var publicDir = "/public"
		var scanPaths = "/WEB-INF/classes"
	}

}

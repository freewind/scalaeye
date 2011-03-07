package org.scalaeye

import net.lag.configgy._
import net.lag.logging.Logger
import org.scalaeye._

object AppConfig extends Init {

	private var _mode: String = System.getProperty("mode") || "dev"
	private var config: Config = _

	def load(mode: String = this.mode) {
		this._mode = mode
		Configgy.configure(Context.classesDir / (mode+".conf"))
		this.config = Configgy.config
	}

	def mode = _mode

	override def init() {
		this.load()
	}

	def getRoot: Config = config
	def get(key: String): Option[ConfigMap] = config.getConfigMap(key)
	def app: ConfigMap = { config.configMap("app") }
	def db: ConfigMap = { config.configMap("db") }
	def log: ConfigMap = { config.configMap("log") }
	def plugins: ConfigMap = { config.configMap("plugins") }
	def cache: ConfigMap = { config.configMap("cache") }
	def mail: ConfigMap = { config.configMap("mail") }

	def inDev: Boolean = this.mode == "dev"
	def inTest: Boolean = this.mode == "test"
	def inProd: Boolean = this.mode == "prod"

}

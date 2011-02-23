// 如果controller比较少，可以直接都写在这个文件里；否则可再建立各自的controller文件
package controllers


class App extends Controller {

	def index() {
		render()
	}

}

// controller类，默认对应的url为类名的小写，如本例，Users对应/users。可通过注解改变
@any("/webusers")
class Users extends Controller {

	// 在每一个action调用之前被调用，使用@before注解
	@before
	def before = {
		'currentUser = getCurrentUser
	}

	// 在每一个action调用之后被调用，使用@after注解
	@after
	def after = {
		// 清理工作
	}

	// 当出现action中的代码出错时
	@catch
	def sendErrorEmail = {
			// send mail
	}

	// action，默认对应的url为函数名的小写，如此处对应/webusers下的/index。也可通过注解改变。
	@any("/")
	def index = "This is the index page of users"

	// 函数名中的参数将被自动赋值
	def show(id: Long) = {
		'user = User.get(id) // 通过符号，简化向模板中注入变量，等于context("user") = User.get(id)
		render() // 默认将渲染views/users/show.xxx，也可通过参数改变路径
	}

	@get
	def newPage = {
		render()
	}

	@post("/create")
	def create(email:String, password: String, passwordConfirm: String) = {
		// 通过check函数，检查各参数
		try {
			check(email).notBlank.email.unique {
				User.findByEmail(email) == null
			}
			check(password).notBlank.minLength(6).maxLength(20)
			check(passwordConfirm).equalTo(password)
		} catch(e:ValidationException) {
			// 如果失败，转向表单输入页面
			newPage()
		}

		val user = new User(email, password)
		user.save()
		flash.message("创建成功")
		show(user.id) // 成功后，转向显示页面
	}

}

package models

import javax.persistence._
import com.avaje.ebean._, annotation._
import org.scalaeye._, dao._

@Entity
@Table(name = "users")
class User extends EbeanEntity {

	@Id
	var id: Int = _

	@Column(length = 60, unique = true, nullable = false)
	var email: String = _

	@Column(length = 60, nullable = false)
	var name: String = _

	@Column(length = 32, nullable = false)
	var password: String = _

	@OneToMany(mappedBy = "author", cascade = Array(CascadeType.ALL))
	var questions: JList[Question] = _

	@OneToMany(mappedBy = "author", cascade = Array(CascadeType.ALL))
	var answers: JList[Answer] = _
}

object User extends EbeanDao[User]


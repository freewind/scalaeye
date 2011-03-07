package models

import javax.persistence._
import com.avaje.ebean._, annotation._
import org.scalaeye._, dao._

@Entity
@Table(name = "tags")
class Tag extends EbeanEntity {

	@Id
	var id: Long = _

	@Column(length = 100, nullable = false, unique = true)
	var name: String = _

	@ManyToMany(cascade = Array(CascadeType.ALL))
	@JoinTable(name = "question_tag_r")
	var questions: JList[Question] = _

}

object Tag extends EbeanDao[Tag]

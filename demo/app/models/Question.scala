package models

import javax.persistence._
import com.avaje.ebean._, annotation._
import org.scalaeye._, dao._
import java.util.Date

@Entity
@Table(name = "questions")
class Question extends EbeanEntity {

	@Id
	var id: Long = _

	@Column(length = 300, nullable = false)
	var title: String = _

	@Column(columnDefinition = "Text", nullable = false)
	var content: String = _

	var createAt: Date = new Date

	@ManyToOne
	var author: User = _

	@ManyToMany(cascade = Array(CascadeType.ALL))
	@JoinTable(name = "question_tag_r")
	var tags: JList[Tag] = _

}

object Question extends EbeanDao[Question]

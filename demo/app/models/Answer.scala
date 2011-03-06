package models

import javax.persistence._
import com.avaje.ebean._, annotation._
import org.scalaeye._, dao._
import java.util.Date

@Entity
@Table(name = "answers")
class Answer extends EbeanEntity {

	@Id
	var id: Long = _

	@Column(columnDefinition = "Text", nullable = false)
	var content: String = _

	var createAt: Date = new Date

	@ManyToOne
	var author: User = _

	@ManyToOne
	var question: Question = _

}

object Answer extends EbeanDao[Answer]

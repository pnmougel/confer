package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class Comment(id: Long, userId: Long, conferenceId: Long, content: String, date: Date)

object Comment {

    val comment = {
        get[Long]("id") ~ get[Long]("userId") ~ get[Long]("conference_id") ~ get[String]("content") ~ get[Date]("date") map {
            case id ~ userId ~ conferenceId ~ content ~ date => Comment(id, userId, conferenceId, content, date)
        }
    }
    
    def getById(id: Long) : Option[Comment] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM comment WHERE id = {id}").on('id -> id).as(Comment.comment.singleOpt)
    }
    
    def getByDate(date : Date) : Option[Comment] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM comment WHERE date = {date}").on('date -> date).as(Comment.comment.singleOpt)
    }
    
    def getByConferenceId(conferenceId : Long): List[Comment] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM comment WHERE conference_id = {conferenceId}").on('conferenceId -> conferenceId).as(comment *)
    }

    
    def all(): List[Comment] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM comment").as(comment *)
    }

    def create(conferenceId : Long, userId : Long, content: String, date: Date = new Date()) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO comment (iuser_id, conference_id, content, date) values " +
            		"({userId}, {conferenceId}, {content}, {date})").on(
                'userId -> userId,
                'conferenceId -> conferenceId,
                'content -> content,
                'date -> date).executeUpdate()
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM comment WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }

}
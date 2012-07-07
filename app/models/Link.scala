package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class Link(id: Long, conferenceId: Long, url: String, label: String)

object Link {

    val link = {
        get[Long]("id") ~ get[Long]("conference_id") ~ get[String]("url") ~ get[String]("name") map {
            case id ~ conferenceId ~ url ~ label => Link(id, conferenceId, url, label)
        }
    }
    
    def getByDate(date : Date) : Option[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE date = {date}").on('date -> date).as(Link.link.singleOpt)
    }
    
    def getById(id: Long) : Option[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE id = {id}").on('id -> id).as(Link.link.singleOpt)
    }
    
    def getByConferenceId(conferenceId: Long) : List[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE conference_id = {id}").on('id -> conferenceId).as(link *)
    }
    
    def all(): List[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link").as(link *)
    }
    
    def create(conferenceId : Long, url: String, label: String, date : Date) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO link (conference_id, url, name, date) values ({conferenceId}, {url}, {label}, {date})").on(
                'conferenceId -> conferenceId,
                'url -> url,
                'label -> label,
                'date -> date).executeUpdate()
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM link WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM link").executeUpdate()
        }
    }
}
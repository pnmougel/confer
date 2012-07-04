package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Link(id: Long, conferenceId: Long, url: String, name: String)

object Link {

    val link = {
        get[Long]("id") ~ get[Long]("conference_id") ~ get[String]("url") ~ get[String]("name") map {
            case id ~ conferenceId ~ url ~ name => Link(id, conferenceId, url, name)
        }
    }
    
    def getById(id: Long) : Option[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE id = {id}").on('id -> id).as(Link.link.singleOpt)
    }
    
    def getByConfIdAndUrl(conferenceId: Long, url: String) : Option[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE conference_id = {id} AND url = {url}").on('id -> conferenceId, 'url -> url).as(Link.link.singleOpt)
    }
    
    def getByConferenceId(conferenceId: Long) : List[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link WHERE conference_id = {id}").on('id -> conferenceId).as(link *)
    }
    
    def all(): List[Link] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM link").as(link *)
    }

    def create(conferenceId : Long, url: String, name: String = "") {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO link (conference_id, url, name) values ({conferenceId}, {url}, {name})").on(
                'conferenceId -> conferenceId,
                'url -> url,
                'name -> name).executeUpdate()
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM link WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }

}
package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class Link(id: Long, conferenceId: Long, url: String, label: String)

object Link extends Table[Link] {

    val tableName = "link"
        
    val single = {
        get[Long]("id") ~ get[Long]("conference_id") ~ get[String]("url") ~ get[String]("name") map {
            case id ~ conferenceId ~ url ~ label => Link(id, conferenceId, url, label)
        }
    }
    
    def setRelatedToConference(originalConferenceId : Long, newConferenceId : Long) = DB.withConnection { implicit c =>
        SQL("UPDATE link SET conference_id = {newConferenceId} WHERE conference_id = {originalConferenceId}").on(
                'newConferenceId -> newConferenceId,
                'originalConferenceId -> originalConferenceId).executeUpdate()
    }
    
    def getByDate(date : Date) : Option[Link] = findOneBy[Date](date, "date")
    
    def getByConferenceId(conferenceId: Long) : List[Link] = findBy[Long](conferenceId, "conference_id")
    
    def create(conferenceId : Long, url: String, label: String, date : Date) : Long = {
        build('conference_id -> conferenceId, 'url -> url, 'name -> label, 'date -> date)
    }
}
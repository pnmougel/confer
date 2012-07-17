package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class ConferenceRelation(conferenceFromId: Long, conferenceToId: Long, relationTypeId: Option[Long])

object ConferenceRelation extends Table[ConferenceRelation] {

    val single = {
        get[Long]("conference_from_id") ~ get[Long]("conference_to_id") ~ get[Option[Long]]("conference_relation_type_id") map {
            case conferenceFromId ~ conferenceToId ~ relationTypeId => {
                ConferenceRelation(conferenceFromId, conferenceToId, relationTypeId)
            }
        }
    }
    
    val tableName = "conference_relation"
    
    def getConferencesRelatedTo(conferenceId: Long) : List[Conference] = DB.withConnection { implicit c =>
        SQL("""
                SELECT conference.* FROM conference, conference_relation 
                WHERE conference_from_id = {id} AND conference_to_id = conference.id
                """).on('id -> conferenceId).as(Conference.conference *)
    }
    
    def create(conferenceFromId: Long, conferenceToId: Long) : Long = {
		build('conference_from_id -> conferenceFromId, 'conference_to_id -> conferenceToId)
		build('conference_from_id -> conferenceToId, 'conference_to_id -> conferenceFromId)
    }
            
    def create(conferenceFromId: Long, conferenceToId: Long, relationTypeId : Long) : Long = {
        build('conference_from_id -> conferenceFromId, 'conference_to_id -> conferenceToId, 'conference_relation_type_id -> relationTypeId)
        build('conference_from_id -> conferenceToId, 'conference_to_id -> conferenceFromId, 'conference_relation_type_id -> relationTypeId)
    } 
}
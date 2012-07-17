package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class ExternalRanking(id: Long, url: String, name: String, description : String)

object ExternalRanking extends Table[ExternalRanking] {

    val tableName = "external_ranking"
        
    val single = {
        get[Long]("id") ~ get[String]("url") ~ get[String]("name") ~ get[String]("description") map {
            case id ~ url ~ name ~  description => ExternalRanking(id, url, name, description)
        }
    }
    
    def create(url: String, name: String, description: String) : Long = {
        build('url -> url, 'name -> name, 'description -> description)
    }
}
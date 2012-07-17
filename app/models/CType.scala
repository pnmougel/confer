package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class CType(id: Long, name: String)

object CType extends Table[CType] {

    val tableName = "ctype"
        
    val single = {
        get[Long]("id") ~ get[String]("name") map {
            case id ~ name => CType(id, name)
        }
    }
        
    def create(name: String) : Long = {
        build('name -> name)
    }
}
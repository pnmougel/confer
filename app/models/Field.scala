package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Field(id: Long, name: String)

object Field extends Table[Field] {
	
    val tableName = "field"
    
    val single = {
        get[Long]("id") ~ get[String]("name") map {
            case id ~ name => Field(id, name)
        }
    }
    
    def getByName(name: String) : Option[Field] = findOneBy[String](name, "name")
    
    override def all(): List[Field] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM field ORDER BY name").as(single *)
    }

    def create(name: String) : Long = build('name -> name)
}
package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Field(id: Long, name: String)

object Field {

    val field = {
        get[Long]("id") ~ get[String]("name") map {
            case id ~ name => Field(id, name)
        }
    }
    
    def getByName(name: String) : Option[Field] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM field WHERE name = {name} LIMIT 1").on('name -> name).as(Field.field.singleOpt)
    }
    
    def getById(id: Long) : Option[Field] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM field WHERE id = {id} LIMIT 1").on('id -> id).as(Field.field.singleOpt)
    }
    
    def all(): List[Field] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM field ORDER BY name").as(field *)
    }

    def create(name: String) : Long = {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO field (name) values ({name})").on(
                'name -> name).executeInsert().get
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM field WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM field").executeUpdate()
        }
    }
}
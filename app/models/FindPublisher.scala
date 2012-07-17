package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Publisher(id: Long, name: String)

object Publisher {

    val publisher = {
        get[Long]("id") ~ get[String]("name") map {
            case id ~ name => Publisher(id, name)
        }
    }
    
    def getByName(name: String) : Option[Publisher] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM publisher WHERE name = {name} LIMIT 1").on('name -> name).as(Publisher.publisher.singleOpt)
    }
    
    def getById(id: Long) : Option[Publisher] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM publisher WHERE id = {id} LIMIT 1").on('id -> id).as(Publisher.publisher.singleOpt)
    }
    
    def all(): List[Publisher] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM publisher ORDER BY name").as(publisher *)
    }

    def create(name: String) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO publisher (name) values ({name})").on(
                'name -> name).executeUpdate()
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM publisher WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM publisher").executeUpdate()
        }
    }
}
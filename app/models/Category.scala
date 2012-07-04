package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Category(id: Pk[Long], name: String)

object Category {

    val category = {
        get[Pk[Long]]("id") ~ get[String]("name") map {
            case id ~ name => Category(id, name)
        }
    }
    
    def getByName(name: String) : Option[Category] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM category WHERE name = {name} LIMIT 1").on('name -> name).as(Category.category.singleOpt)
    }
    
    def getById(id: Long) : Option[Category] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM category WHERE id = {id} LIMIT 1").on('id -> id).as(Category.category.singleOpt)
    }
    
    
    def all(): List[Category] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM category ORDER BY name").as(category *)
    }

    def create(name: String) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO category (name) values ({name})").on(
                'name -> name).executeUpdate()
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM category WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }

}
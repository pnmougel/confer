package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class SubField(id: Long, name: String, fieldId : Long)

object SubField {

    val subField = {
        get[Long]("id") ~ get[String]("name") ~ get[Long]("field_id") map {
            case id ~ name ~ fieldId => SubField(id, name, fieldId)
        }
    }
    
    def getByName(name: String) : Option[SubField] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM subfield WHERE name = {name} LIMIT 1").on('name -> name).as(SubField.subField.singleOpt)
    }
    
    def getById(id: Long) : Option[SubField] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM subfield WHERE id = {id} LIMIT 1").on('id -> id).as(SubField.subField.singleOpt)
    }
    
    def getByField(id: Long) : List[SubField] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM subfield WHERE field_id = {id} ORDER BY name").on('id -> id).as(subField *)
    }
    
    def all(): List[SubField] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM subfield ORDER BY name").as(subField *)
    }

    def create(name: String, fieldId : Long) : Long = {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO subfield (name, field_id) values ({name}, {fieldId})").on(
                'name -> name,
                'fieldId -> fieldId).executeInsert().get
        }
    }

    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM subfield WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM subfield").executeUpdate()
        }
    }
}
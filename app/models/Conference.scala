package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Conference(id: Long, name: String, shortName: Option[String], 
        yearSince: Option[Int], description: Option[String], 
        isNational: Boolean, isWorkshop: Boolean, isJournal: Boolean, 
        category: Long, publisher: Long, categoryName: String, conferenceType : String,
        publisherName : String)

object Conference {
	// This is very ugly, it probably should be better in a table in the database...
    val typeMappingIntToBoolean = Map[Int, (Boolean, Boolean, Boolean)](
                        1 -> (false, false, true),
                        2 -> (false, false, false),
                        3 -> (true, false, false),
                        4 -> (false, true, false),
                        5 -> (true, true, false))
    val typeMappingBooleanToInt = Map[(Boolean, Boolean, Boolean), Int](
                        (false, false, true) -> 1,
                        (false, false, false) -> 2,
                        (true, false, false) -> 3,
                        (false, true, false) -> 4,
                        (true, true, false) -> 5)
    val typeMappingIntToString = Map[Int, String](
                        1 -> "Journal",
                        2 -> "International Conference",
                        3 -> "National Conference",
                        4 -> "International Workshop",
                        5 -> "National Workshop")
    
                        
    val conference = {
        get[Long]("id") ~ 
        get[String]("name") ~
        get[Option[String]]("short_name") ~
        get[Option[Int]]("year_since") ~
        get[Option[String]]("description") ~
        get[Boolean]("is_national") ~ 
        get[Boolean]("is_workshop") ~
        get[Boolean]("is_journal") ~
        get[Long]("subfield_id") ~
        get[Long]("publisher_id") map {
            case id ~ name ~ shortName ~ yearSince ~ description ~ isNational ~ isWorkshop ~ isJournal ~ category ~ publisher =>
                val conferenceType = typeMappingIntToString(typeMappingBooleanToInt((isNational, isWorkshop, isJournal)))
                val categoryName = SubField.getById(category).get
                val publisherName = Publisher.getById(publisher).get.name
                Conference(
                    id, name, shortName, yearSince, description, isNational, isWorkshop, isJournal, category, 
                    publisher, categoryName.name, conferenceType, publisherName)
        }
    }

    def all() : List[Conference] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM Conference").as(conference *)
    }

    def create(name: String, shortName: String = "", yearSince: Int = 0, 
            description: String = "", isNational: Boolean, isWorkshop: Boolean, isJournal: Boolean, 
            field: Field, publisher: Publisher) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO Conference (name, short_name, description, year_since, " +
            		"is_national, is_workshop, is_journal, subfield_id, publisher_id) " +
            		"values ({name}, {shortName}, {description}, {yearSince}, " +
            		"{isNational}, {isWorkshop}, {isJournal}, " +
            		"{category}, {publisher})").on(
                'name -> name,
                'shortName -> shortName,
                'description -> description,
                'yearSince -> yearSince,
                'isNational -> isNational,
                'isWorkshop -> isWorkshop,
                'isJournal -> isJournal,
                'category -> field.id,
                'publisher -> publisher.id
                ).executeUpdate()
        }
    }
    
    def create(name: String, shortName: String,  
            isNational: Boolean, isWorkshop: Boolean, isJournal: Boolean, 
            fieldId: Long) : Long = {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO Conference (name, short_name, " +
            		"is_national, is_workshop, is_journal, subfield_id, publisher_id) " +
            		"values ({name}, {shortName}, " +
            		"{isNational}, {isWorkshop}, {isJournal}, " +
            		"{field}, 1)").on(
                'name -> name,
                'shortName -> shortName,
                'isNational -> isNational,
                'isWorkshop -> isWorkshop,
                'isJournal -> isJournal,
                'field -> fieldId
                ).executeInsert().get
        }
    }
    
    def count() : Long = DB.withConnection { implicit c =>
        val firstRow = SQL("SELECT count(*) as c FROM Conference").apply().head
        return firstRow[Long]("c")
    }
    
    def getById(id : Long) : Option[Conference] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM Conference WHERE id = {id}").on('id -> id).as(Conference.conference.singleOpt)
    }
    
    def search(query: String) : List[Conference] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM Conference WHERE name ILIKE {query} OR short_name ILIKE {query}").on(
                'query -> ("%" + query + "%")).as(conference *)
    }
    
    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM Conference WHERE id = {id}").on('id -> id).executeUpdate()
        }
    }
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM Conference").executeUpdate()
        }
    }
}
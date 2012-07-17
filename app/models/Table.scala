package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date
import java.text.SimpleDateFormat

abstract class Table[T] {
    def all(): List[T] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM " + tableName).as(single *)
    }
    
    def findById(id : Long): Option[T] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM " + tableName + " WHERE " + idColumn + " = {id}").on('id -> id).as(single.singleOpt)
    }
    
    def findOneBy[V](value : V, column : String): Option[T] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM " + tableName + " WHERE " + column + " = {value} LIMIT 1").on('value -> value).as(single.singleOpt)
    }
    
    def findBy[V](value : V, column : String, options : List[(String, String)] = List()): List[T] = DB.withConnection { implicit c =>
        val optionsStr = options.map { option => option._1 + " " + option._2}.mkString(" ")
        SQL("SELECT * FROM " + tableName + " WHERE " + column + " = {value} " + optionsStr).on('value -> value).as(single *)
    }
    
    def build(params : (Any, ParameterValue[_])*): Long = DB.withConnection { implicit c =>
        val columnNames = params.map { 
            case (s: Symbol, v) => s.name
            case (k, v) => k.toString
        }
        val columns = columnNames.mkString(" (", ",", ")")
        val values = columnNames.mkString("({", "},{", "})")
        SQL("INSERT INTO " + tableName + columns + " values " + values).on(params:_*).executeInsert().get
    }
    
    def delete(id: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM " + tableName + " WHERE " + idColumn + " = {id}").on('id -> id).executeUpdate()
        }
    }
    
    
    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM " + tableName).executeUpdate()
        }
    }
    
    val idColumn = "id"
        
    val tableName : String
    
    val single : RowParser[T]
    
    def main(args : Array[String]) : Unit = {
        println("test")
    }
}
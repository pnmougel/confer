package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.security.MessageDigest
import java.math.BigInteger

case class User(id: Long, email: String, password: String, pseudo: Option[String], isAdmin: Boolean)

object User {
    // -- Parsers
    /**
     * Parse a User from a ResultSet
     */
    val user = {
        get[Long]("id") ~
        get[String]("email") ~
        get[String]("password") ~
        get[Option[String]]("pseudo") ~
        get[Boolean]("isadmin") map {
            case id ~ email ~ password ~ pseudo ~ isAdmin => User(id, email, password, pseudo, isAdmin)
        }
    }

    // -- Queries
    /**
     * Retrieve a User from email.
     */
    def findByEmail(email: String): Option[User] = DB.withConnection { implicit connection =>
        SQL("select * from iuser where email = {email}").on(
            'email -> email).as(User.user.singleOpt)
    }
    
    def findById(id: Long): Option[User] = DB.withConnection { implicit connection =>
        SQL("select * from iuser where id = {id}").on(
            'id -> id).as(User.user.singleOpt)
    }
    
    
    /**
     * Retrieve all users.
     */
    def findAll: Seq[User] = DB.withConnection { implicit connection =>
        SQL("select * from iuser").as(User.user *)
    }

    def hashPassword(password : String) : String = {
        val digest = MessageDigest.getInstance("SHA").digest(password.getBytes())
        val number = new BigInteger(1, digest)
        number.toString(16).toUpperCase()
    }
    
    /**
     * Authenticate a User.
     */
    def authenticate(email: String, password: String): Option[User] = DB.withConnection { implicit connection =>
        SQL("select * from iuser where email = {email} and password = {password}").on(
                'email -> email,
                'password -> hashPassword(password)).as(User.user.singleOpt)
    }

    /**
     * Create a User.
     */
    def create(email: String, password: String, pseudo: String) : Long = DB.withConnection { implicit connection =>
    	val digest = MessageDigest.getInstance("SHA").digest(password.getBytes()).toString()
    	println("Create digest: " + digest)
        SQL("insert into iuser (email, password, pseudo, isadmin) values ({email}, {password}, {pseudo}, FALSE)").on(
                'email -> email,
                'password -> hashPassword(password),
                'pseudo -> pseudo).executeInsert().get
        
    }

    def deleteAll() {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM iuser").executeUpdate()
        }
    }
}

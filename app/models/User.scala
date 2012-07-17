package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.security.MessageDigest
import java.math.BigInteger

case class User(id: Long, email: String, password: String, pseudo: Option[String], isAdmin: Boolean)

object User extends Table[User] {
    // -- Parsers
    /**
     * Parse a User from a ResultSet
     */
    val single = {
        get[Long]("id") ~
        get[String]("email") ~
        get[String]("password") ~
        get[Option[String]]("pseudo") ~
        get[Boolean]("isadmin") map {
            case id ~ email ~ password ~ pseudo ~ isAdmin => User(id, email, password, pseudo, isAdmin)
        }
    }
    
    val tableName = "iuser"
    
    // -- Queries
    /**
     * Retrieve a User from email.
     */
    def findByEmail(email: String): Option[User] = DB.withConnection { implicit connection =>
        SQL("select * from iuser where email = {email}").on(
            'email -> email).as(User.single.singleOpt)
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
                'password -> hashPassword(password)).as(User.single.singleOpt)
    }

    /**
     * Create a User.
     */
    def create(email: String, password: String, pseudo: String) : Long = build('email -> email, 'password -> hashPassword(password), 'pseudo -> pseudo, 'isadmin -> false)
}

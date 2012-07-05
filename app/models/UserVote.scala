package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class UserVote(conferenceId: Long, userId: Long, score: Int, date: Date)

case class UserVotes(totalVotes : Int, voteA : (Int, Int), voteB : (Int, Int), voteC : (Int, Int), voteD : (Int, Int))


object UserVote {

    val userVote = {
        get[Long]("conference_id") ~ get[Long]("user_id") ~ get[Int]("score") ~ get[Date]("date") map {
            case conferenceId ~ userId ~ score ~ date => UserVote(conferenceId, userId, score, date)
        }
    }
    
    val userVotes = {
        get[Int]("nbVoteA") ~ get[Int]("nbVoteB") ~ get[Int]("nbVoteC") ~ get[Int]("nbVoteD") map {
            case nbVoteA ~ nbVoteB ~ nbVoteC ~ nbVoteD => {
                val totalVotes = nbVoteA + nbVoteB + nbVoteC + nbVoteD
                val percVoteA = nbVoteA / totalVotes * 100 
                val percVoteB = nbVoteB / totalVotes * 100
                val percVoteC = nbVoteC / totalVotes * 100
                val percVoteD = nbVoteD / totalVotes * 100
                UserVotes(totalVotes, (nbVoteA, percVoteA), (nbVoteB, percVoteB), (nbVoteC, percVoteC), (nbVoteD, percVoteD))
            }
        }
    }
    
    /*
    def getUserVotesByConferenceId(conferenceId: Long) : UserVotes = {
        val votes = getByConferenceId(conferenceId)
        
    }
    */
    
    def getByConferenceId(conferenceId: Long) : List[UserVote] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM scoreiuser WHERE conference_id = {id}").on('id -> conferenceId).as(userVote *)
    }
    
    def all(): List[UserVote] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM scoreiuser").as(userVote *)
    }
    
    def create(conferenceId : Long, userId: Long, score: Int, date : Date) {
        DB.withConnection { implicit c =>
            SQL("INSERT INTO scoreiuser (conference_id, iuser_id, score, date) values " +
            		"({conferenceId}, {userId}, {score}, {date})").on(
                'conferenceId -> conferenceId,
                'userId -> userId,
                'score -> score,
                'date -> date).executeUpdate()
        }
    }

    def delete(conferenceId: Long, userId: Long) {
        DB.withConnection { implicit c =>
            SQL("DELETE FROM scoreiuser WHERE conference_id = {conferenceId: Long} AND iuser_id = {}").on(
                    'conferenceId -> conferenceId,
                    'userId -> userId).executeUpdate()
        }
    }

}
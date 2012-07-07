package models
import scala.collection.mutable.ListBuffer
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date

case class UserVote(conferenceId: Long, userId: Long, score: Int, date: Date)

case class UserVotes(avgUserScore : Double, totalVotes : Int, voteA : (Int, Int), voteB : (Int, Int), voteC : (Int, Int), voteD : (Int, Int))


object UserVote {

    val userVote = {
        get[Long]("conference_id") ~ get[Long]("iuser_id") ~ get[Int]("score") ~ get[Date]("date") map {
            case conferenceId ~ userId ~ score ~ date => UserVote(conferenceId, userId, score, date)
        }
    }
    
    def getUserVotesByConferenceId(conferenceId: Long) : UserVotes = {
        var nbVoteA = 0
        var nbVoteB = 0
        var nbVoteC = 0
        var nbVoteD = 0
        getByConferenceId(conferenceId).foreach { vote =>
            if(vote.score == 1) {
                nbVoteA += 1
            }
            if(vote.score == 2) {
                nbVoteB += 1
            }
            if(vote.score == 3) {
                nbVoteC += 1
            }
            if(vote.score == 4) {
                nbVoteD += 1
            }
        }
        val totalVotes : Int = nbVoteA + nbVoteB + nbVoteC + nbVoteD
        val percVoteA = 1.0 * nbVoteA / totalVotes * 100
        val percVoteB = 1.0 * nbVoteB / totalVotes * 100
        val percVoteC = 1.0 * nbVoteC / totalVotes * 100
        val percVoteD = 1.0 * nbVoteD / totalVotes * 100
        val avgUserScore = if(totalVotes == 0) {
            -1
        } else {
            Math.max(0, (-1 * nbVoteD + nbVoteC + 2 * nbVoteB + 3 * nbVoteA) * 5.0 / (3 * totalVotes))
        }
        UserVotes(avgUserScore, totalVotes, (nbVoteA, percVoteA.toInt), (nbVoteB, percVoteB.toInt), (nbVoteC, percVoteC.toInt), (nbVoteD, percVoteD.toInt))
    }
    
    def hasVotedForConference(conferenceId: Long, userId: Long) : Boolean = DB.withConnection { implicit c =>
        val row = SQL("SELECT * FROM scoreiuser WHERE conference_id = {id} AND iuser_id = {userId}").on(
                'id -> conferenceId,
                'userId -> userId).as(userVote.singleOpt)
        row.isDefined
    }
    
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
            SQL("DELETE FROM scoreiuser WHERE conference_id = {conferenceId} AND iuser_id = {userId}").on(
                    'conferenceId -> conferenceId,
                    'userId -> userId).executeUpdate()
        }
    }

}
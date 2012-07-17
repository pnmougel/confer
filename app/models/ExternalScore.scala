package models
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

case class ExternalScore(conferenceId: Long, externalRanking : ExternalRanking, originalScore : Int, score: Double, scoreText : String)


object ExternalScore extends Table[ExternalScore] {

    val single = {
        get[Long]("ranking_id") ~ get[Long]("conference_id") ~ get[Int]("score") map {
            case rankingId ~ conferenceId ~ score => {
                ExternalScore(conferenceId, ExternalRanking.findById(rankingId).get, score, getRealScore(rankingId, score), getScoreText(rankingId, score))
            }
        }
    }
    
    private def getRealScore(rankingId : Long, score : Int) = rankingId match {
        case 2 => {
            getAstarScore(score)
        }
        case _ => {
            -1
        }
    }
    
    private def getAstarScore(score : Int) = score match {
        case 4 => 5
        case 3 => 4.5
        case 2 => 3.5
        case 1 => 2
        case _ => -1
    }
    
    private def getScoreText(rankingId : Long, score : Int) = rankingId match {
        case 2 => {
            getAstarScoreText(score)
        }
        case _ => {
            ""
        }
    }
    
    private def getAstarScoreText(score : Int) = score match {
        case 4 => "Rank A+"
        case 3 => "Rank A"
        case 2 => "Rank B"
        case 1 => "Rank C"
        case _ => ""
    }
    
    
    val tableName = "external_ranking_conference"
    
    def getByConferenceId(conferenceId: Long) : List[ExternalScore] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM external_ranking_conference WHERE conference_id = {id}").on('id -> conferenceId).as(single *)
    }
    
    def getRankingForConference(conferenceId: Long, rankingId : Long) : Option[ExternalScore] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM external_ranking_conference WHERE conference_id = {id} AND ranking_id = {rankingId}").on(
                'id -> conferenceId,
                'rankingId -> rankingId).as(single.singleOpt)
    }
    
    def clearCore() = DB.withConnection { implicit c =>
        SQL("DELETE FROM external_ranking_conference WHERE ranking_id = 2").executeUpdate()
    }
    
    def deleteByConference(conferenceId : Long) = DB.withConnection { implicit c =>
        SQL("DELETE FROM external_ranking_conference WHERE conference_id = {conferenceId}").on(
                'conferenceId -> conferenceId).executeUpdate()
        Conference.updateExternalScoreForConference(conferenceId)
    }
    
    def updateScore(conferenceId : Long, rankingId: Long, score: Int) = DB.withConnection { implicit c =>
        SQL("UPDATE external_ranking_conference SET score = {score} WHERE conference_id = {conferenceId} AND ranking_id = {rankingId}").on(
                'conferenceId -> conferenceId,
                'score -> score,
                'rankingId -> rankingId).executeUpdate()
        Conference.updateExternalScoreForConference(conferenceId)
    }
    
    def create(conferenceId : Long, rankingId: Long, score: Int) : Long = {
        val scoreId = build('conference_id -> conferenceId, 'ranking_id -> rankingId, 'score -> score)
        Conference.updateExternalScoreForConference(conferenceId)
        scoreId
    } 
}
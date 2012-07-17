package controllers.admin

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.MultiMap
import models.Comment
import models.Conference
import models.ConferenceRelation
import models.ExternalScore
import models.Link
import models.SubField
import play.api.data.Forms._
import play.api.mvc._
import play.api._
import models.UserVote

/**
 * Merge conference with similar short name
 */
object Merge extends Controller {
	
    def index = Action {
        var shortNameMap = new HashMap[(String, Long, Long), ListBuffer[Conference]]
        
        Conference.all().foreach { conference =>
            if(conference.shortName.isDefined) {
                if(conference.shortName.get != "") {
                	shortNameMap.getOrElseUpdate((conference.shortName.get, conference.field.id, conference.cType.id), new ListBuffer[Conference]()) += conference
                }
            }
        }
        println("Finished building the map...")
        shortNameMap.foreach { case (shortName, conferences) =>
            if(conferences.size > 1) {
                val firstConf = conferences(0)
                var isFirst = true
                conferences.foreach { conference =>
                    if(isFirst) {
                        isFirst = false
                    } else {
                        mergeConference(firstConf, conference)
                    }
                }
            }
        }
        
    	Ok("Index")
        // Redirect(routes.Admin.list)
    }
    
    def mergeByShortName = Action {
        Ok
    }
    
    def relateConference(conf1 : Conference, conf2 : Conference) = {
        ConferenceRelation.create(conf1.id, conf2.id)
    }
    
    def mergeConference(conf1 : Conference, conf2 : Conference) = {
        if(conf1.name.equalsIgnoreCase(conf2.name)) {
            val hIndex1 = if(conf1.hindex.isDefined) conf1.hindex.get else 0
	        val hIndex2 = if(conf2.hindex.isDefined) conf2.hindex.get else 0
	        val hIndex = scala.math.max(hIndex1, hIndex2)
	        
	        val nbArticles1 = if(conf1.nbArticles.isDefined) conf1.nbArticles.get else 0
	        val nbArticles2 = if(conf2.nbArticles.isDefined) conf2.nbArticles.get else 0
	        val nbArticles = nbArticles1 + nbArticles2

	        // Update the publisher
	        if(conf2.publisher.isDefined && !conf1.publisher.isDefined) {
	            Conference.updatePublisher(conf1.id, conf2.publisher.get.id)
	        }
	        
            // Update the associated information
            Comment.setRelatedToConference(conf2.id, conf1.id)
            Link.setRelatedToConference(conf2.id, conf1.id)

            // Dirty hack to avoid duplicates in subfields
            val subFieldConf1 = Conference.getSubFields(conf1.id).map(_.id).toList
            val subFieldConf2 = Conference.getSubFields(conf2.id).map(_.id).toList
            subFieldConf1.foreach { subFieldId =>
	            if(subFieldConf2.contains(subFieldId)) {
	                Conference.removeSubField(conf1.id, subFieldId)
	            }
	        }
            SubField.setRelatedToConference(conf2.id, conf1.id)
            
            // Update the external rankings
            var externalScores1 = new HashMap[Long, ExternalScore]()
            var externalScores2 = new HashMap[Long, ExternalScore]()
            ExternalScore.getByConferenceId(conf1.id).foreach { externalScore =>
                externalScores1(externalScore.externalRanking.id) = externalScore
            }
            ExternalScore.getByConferenceId(conf2.id).foreach { externalScore =>
                externalScores2(externalScore.externalRanking.id) = externalScore
            }
            externalScores2.foreach { case (rankingId, externalScore) =>
                if(externalScores1.contains(rankingId)) {
                    if(externalScores1(rankingId).originalScore < externalScore.originalScore) {
                    	ExternalScore.updateScore(conf1.id, rankingId, externalScore.originalScore)
                    }
                } else {
                    ExternalScore.create(conf1.id, rankingId, externalScore.originalScore)
                }
            }
            ExternalScore.deleteByConference(conf2.id)
            
            // Update the users rankings
            var userScores1 = new HashMap[Long, UserVote]()
            var userScores2 = new HashMap[Long, UserVote]()
            UserVote.getByConferenceId(conf1.id).foreach { userScore =>
                userScores1(userScore.userId) = userScore
            }
            UserVote.getByConferenceId(conf2.id).foreach { userScore =>
                userScores2(userScore.userId) = userScore
            }
            userScores2.foreach { case (userId, userScore) =>
                if(!userScores1.contains(userId)) {
                    UserVote.create(conf1.id, userId, userScore.score, userScore.date)
                }
            }
            UserVote.deleteByConference(conf2.id)
            
            if(hIndex != 0) {
            	Conference.updateHIndex(conf1.id, hIndex)
            }
            if(nbArticles != 0) {
            	Conference.updateNbArticles(conf1.id, nbArticles)
            }
	        Conference.delete(conf2.id)
	        
	        println("Merged " + conf1.shortName)
	        
        } else {
        	 relateConference(conf1, conf2)
        }
    }
}
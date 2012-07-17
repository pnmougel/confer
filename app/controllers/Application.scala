package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Field
import models.Conference
import models.Publisher
import models.Link
import models.ConferenceRelation
import models.Comment
import java.util.Date
import org.joda.time.format.ISODateTimeFormat
import models.UserVote
import java.io.File
import models.ExternalScore

object Application extends Controller {
	
    // Global name for the application
    val name = "ColouR"
    
    val addConfForm = Form(
        tuple(
            "fullName" -> text,
            "shortName" -> text,
            "type" -> number,
            "field" -> number))
    
    def index() = Action { implicit request =>
        Ok(views.html.index(Conference.count(), Search.searchForm))
    }
    
    def conference(id: Long) = Action { implicit request =>
        // Retrieve the conference
        val conference = Conference.getById(id)
        
        if (conference.isDefined) {
            val links = Link.getByConferenceId(id)
            val comments = Comment.getByConferenceId(id)
            val userVotes = UserVote.getUserVotesByConferenceId(id)
            val externalScores = ExternalScore.getByConferenceId(id)
            val subFields = Conference.getSubFields(id)
            val relatedConferences = ConferenceRelation.getConferencesRelatedTo(id)
            
            // Update the scores
            // Should not be necessary
            Conference.updateUserScoreForConference(id)
            Conference.updateExternalScoreForConference(id)
            
            var hasVoted = false
            if(request.session.get("userId").isDefined) {
                val userId = request.session.get("userId").get.toInt
                hasVoted = UserVote.hasVotedForConference(id, userId)
            }
            Ok(views.html.conference(conference.get, userVotes, externalScores, subFields, relatedConferences, hasVoted, links, comments))
        } else {
            Ok("Unable to find conference")
        }
    }
    
    def addConf = Action { implicit request =>
        addConfForm.bindFromRequest.fold(
            errors => BadRequest("Unable to create"),
            params => {
                val newConferenceId = Conference.create(params._1, params._2, params._3, params._4)
                Redirect(routes.Application.conference(newConferenceId))
                
            }
        )
    }
}


package controllers.conference

import java.util.Date

import models.UserVote
import play.api.data.Forms.number
import play.api.data.Forms.tuple
import play.api.data.Form
import play.api.mvc.Action
import play.api.mvc.Controller

object Scores extends Controller {
	
    val userScoreForm = Form(
        tuple(
            "conference_id" -> number,
            "user_id" -> number,
            "score" -> number))
    val deleteVoteForm = Form(
        tuple(
            "conference_id" -> number,
            "user_id" -> number))
            
    def addVote = Action { implicit request =>
        userScoreForm.bindFromRequest.fold(
        	errors => BadRequest("How did you manage that ?"),
        	userScore => {
        	    val date = new Date()
        	    UserVote.create(userScore._1, userScore._2, userScore._3, date)
        	    val userVotes = UserVote.getUserVotesByConferenceId(userScore._1)
        	    Ok(views.html.conferences.userScores(userVotes, request))
        	}
        )
    }
    
    def deleteVote = Action { implicit request =>
        deleteVoteForm.bindFromRequest.fold(
        	errors => BadRequest("How did you manage that ?"),
        	userScore => {
        	    UserVote.delete(userScore._1, userScore._2)
        	    val userVotes = UserVote.getUserVotesByConferenceId(userScore._1)
        	    Ok(views.html.conferences.userScores(userVotes, request))
        	}
        )
    }
}


package controllers.conference

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Category
import models.Conference
import models.Publisher
import models.Link
import models.Comment
import java.util.Date
import org.joda.time.format.ISODateTimeFormat
import models.UserVote

object Scores extends Controller {
	
    val userScoreForm = Form(
        tuple(
            "conference_id" -> number,
            "user_id" -> number,
            "score" -> number))
    
    def addComment = Action { implicit request =>
        userScoreForm.bindFromRequest.fold(
        	errors => BadRequest("How did you manage that ?"),
        	userScore => {
        	    val date = new Date()
        	    UserVote.create(userScore._1, userScore._2, userScore._3, date)
        	    Ok("")
        	}
        )
    }
    
    def deleteComment(userId: Long, conferenceId: Long) = Action { 
        UserVote.delete(userId, conferenceId)
        Ok
    }
}


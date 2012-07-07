package controllers.conference

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Conference
import models.Publisher
import models.Link
import models.Comment
import java.util.Date
import org.joda.time.format.ISODateTimeFormat

object Comments extends Controller {
	
    val commentForm = Form(
        tuple(
            "conference_id" -> number,
            "user_id" -> number,
            "content" -> text))
    
    def addComment = Action { implicit request =>
        commentForm.bindFromRequest.fold(
        	errors => BadRequest("How did you manage that ?"),
        	comment => {
        	    val date = new Date()
        	    Comment.create(comment._1, comment._2, comment._3, date)
        	    val insertedComment = Comment.getByDate(date).get
        	    val userId = if(request.session.get("userId").isDefined) request.session.get("userId").get.toInt else -1
        	    Ok(views.html.snippets.comment(insertedComment, userId))
        	}
        )
    }
    
    
    def deleteComment(id: Long) = Action { 
        Comment.delete(id)
        Ok
    }
}


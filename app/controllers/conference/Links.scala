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

object Links extends Controller {
    val addLinkForm = Form(
        tuple(
            "url" -> text,
            "conference_id" -> number,
            "label" -> text))
    
    def addLink = Action {  implicit request => 
        addLinkForm.bindFromRequest.fold(
            errors => BadRequest(""),
            params => {
                val date = new Date()
                Link.create(params._2, params._1, params._3, date)
                val newLink = Link.getByDate(date)
                Ok(views.html.snippets.link(newLink.get))
            }
        )
    }
    
    def deleteLink(id: Long) = Action { 
        Link.delete(id)
        Ok
    }
    
}


package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.Category
import models.Conference
import models.Publisher
import models.Link

object Search extends Controller {
	def addUrl(id: Long) = Action { implicit request =>
        // request.
        request.headers.get("url").get
        Link.create(id, request.headers.get("url").get)
        // Link.create(id, request.queryString("url").toString)
        Ok("")
    }
}
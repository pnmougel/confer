package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Conference
import models.Publisher
import models.Link

object Search extends Controller {
    val searchForm = Form("query" -> text)

    def search = Action { implicit request =>
        searchForm.bindFromRequest.fold(
            errors => BadRequest("How did you manage that ?"),
            query => {
                val conferences = Conference.findByNameAndShortName(query);
		        if(conferences.size == 1) {
		            // Only one conference is matching
		            Redirect(routes.Application.conference(conferences(0).id))
		        } else {
		            // Display the list of all matching conferences
		            val matchingExactly = conferences.filter { conference => 
		                conference.name.equalsIgnoreCase(query) ||
		                conference.shortName.getOrElse("").equalsIgnoreCase(query) 
	                }
		            if(matchingExactly.size == 1) {
		                Redirect(routes.Application.conference(matchingExactly(0).id))
		            } else {
		                Ok(views.html.results(searchForm, query, conferences, models.Field.all))
		            }
		        }
            }
        )
    }
}
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Field
import models.Conference
import models.Publisher
import models.Link
import models.Comment
import java.util.Date
import org.joda.time.format.ISODateTimeFormat
import models.UserVote
import java.io.File

object Application extends Controller {
	
    // Global name for the application
    val name = "Confer"
    
    val searchForm = Form("query" -> text)
    
    val addConfForm = Form(
        tuple(
            "fullName" -> text,
            "shortName" -> text,
            "type" -> number,
            "field" -> number))
    
    def buildData = Action { implicit request =>
        // Add some publishers
        Publisher.create("ACM")
        Publisher.create("Springer")
        Publisher.create("IEEE")
        Publisher.create("SIAM")
        
        // Add some fields
        Field.create("Data Management")
        
        // Add some conferences
        var publisher = Publisher.getByName("ACM")
        var field = Field.getByName("Data Management")
        if (publisher.isDefined && field.isDefined) {
            Conference.create("Pacific Asia Conference on Knowledge Discovery and Data Mining", "PAKDD", 1996, 
                    """The Pacific-Asia Conference on Knowledge Discovery and Data Mining (PAKDD) is a leading international conference in the areas of data mining and knowledge discovery (KDD). It provides an international forum for researchers and industry practitioners to share their new ideas, original research results and practical development experiences from all KDD related areas, including data mining, data warehousing, machine learning, aritificial intelligence, databases, statistics, knowledge engineering, visualization, and decision-making systems. The conference calls for research papers reporting original investigation results and industrial papers reporting real data mining applications and system development experience. """, true, false, false,
                field.get, publisher.get)
        }
        
        // Add some links
        var confPAKDD = Conference.search("PAKDD")
        
        Ok("Data Created, but you better check...")
    }
    
    def index() = Action { implicit request =>
        var cacheMAS = new File("cache/microsoft_AR/")
        cacheMAS.mkdirs()
        var cacheMASTmp = new File("cache/microsoft_AR/tmp")
        cacheMASTmp.createNewFile()
        Ok(views.html.index(Conference.count(), searchForm))
    }
    
    def conference(id: Long) = Action { implicit request =>
        // Retrieve the conference
        val conference = Conference.getById(id)
        
        if (conference.isDefined) {
            val links = Link.getByConferenceId(conference.get.id)
            val comments = Comment.getByConferenceId(conference.get.id)
            val userVotes = UserVote.getUserVotesByConferenceId(conference.get.id)
            var hasVoted = false
            if(request.session.get("userId").isDefined) {
                val userId = request.session.get("userId").get.toInt
                hasVoted = UserVote.hasVotedForConference(id, userId)
            }
            Ok(views.html.conference(conference.get, userVotes, hasVoted, links, comments))
        } else {
            Ok("Unable to find conference")
        }
    }
    
    def addConf = Action { implicit request =>
        addConfForm.bindFromRequest.fold(
            errors => BadRequest("Unable to create"),
            params => {
                val confType = params._3
                val (isNational, isWorkshop, isJournal) = Conference.typeMappingIntToBoolean(confType)
                Conference.create(params._1, params._2, isNational, isWorkshop, isJournal, params._4)
                val newConference = Conference.search(params._1)
                Redirect(routes.Application.conference(newConference(0).id))
                
            }
        )
    }
    
    def search = Action { implicit request =>
        searchForm.bindFromRequest.fold(
            errors => BadRequest("How did you manage that ?"),
            query => {
                val conferences = Conference.search(query);
		        if(conferences.size == 1) {
		            // Only one conference is matching
		            Redirect(routes.Application.conference(conferences(0).id))
		        } else if(conferences.size == 0) {
		            // No results, propose to create a new entry
		            Ok(views.html.noresults(searchForm, query, Field.all))
		        } else {
		            // Display the list of all matching conferences
		            val matchingExactly = conferences.filter { conference => 
		                conference.name.equalsIgnoreCase(query) ||
		                conference.shortName.getOrElse("").equalsIgnoreCase(query) 
	                }
		            if(matchingExactly.size == 1) {
		                Redirect(routes.Application.conference(matchingExactly(0).id))
		            } else {
		                Ok(views.html.results(searchForm, query, conferences, Field.all))
		            }
		        }
            }
        )
    }
    
    
    /*
    // -- Javascript routing
    def javascriptRoutes = Action {
    	import routes.javascript._
    	Ok(Routes.javascriptRouter("jsRoutes")(
    			controllers.routes.javascript.Application.addUrl
		)).as("text/javascript") 
  	}
  	*/
}


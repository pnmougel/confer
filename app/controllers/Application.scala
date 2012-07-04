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
import models.Comment

object Application extends Controller {
	
    val searchForm = Form("query" -> text)
    
    
    val addConfForm = Form(
        tuple(
            "fullName" -> text,
            "shortName" -> text,
            "type" -> number,
            "field" -> number))
    
    
    val addLinkForm = Form(
        tuple(
            "url" -> text,
            "conference_id" -> number))
    
    def addLink = Action {  implicit request => 
        addLinkForm.bindFromRequest.fold(
            errors => BadRequest(""),
            params => {
                Link.create(params._2, params._1)
                // Get the id !!!
                val newLink = Link.getByConfIdAndUrl(params._2, params._1)
                Ok(views.html.snippets.link(Link(newLink.get.id, params._2, params._1, "")))
            }
        )
    }
    
    def deleteLink(id: Long) = Action { 
        Link.delete(id)
        Ok
    }
    
    def buildData = Action { implicit request =>
        // Add some publishers
        Publisher.create("ACM")
        Publisher.create("Springer")
        Publisher.create("IEEE")
        Publisher.create("SIAM")
        
        // Add some fields
        Category.create("Data Management")
        
        // Add some conferences
        var publisher = Publisher.getByName("ACM")
        var field = Category.getByName("Data Management")
        if (publisher.isDefined && field.isDefined) {
            Conference.create("Pacific Asia Conference on Knowledge Discovery and Data Mining", "PAKDD", 1996, 
                    """The Pacific-Asia Conference on Knowledge Discovery and Data Mining (PAKDD) is a leading international conference in the areas of data mining and knowledge discovery (KDD). It provides an international forum for researchers and industry practitioners to share their new ideas, original research results and practical development experiences from all KDD related areas, including data mining, data warehousing, machine learning, aritificial intelligence, databases, statistics, knowledge engineering, visualization, and decision-making systems. The conference calls for research papers reporting original investigation results and industrial papers reporting real data mining applications and system development experience. """, true, false, false,
                field.get, publisher.get)
        }
        
        // Add some links
        var confPAKDD = Conference.search("PAKDD")
        if(confPAKDD.size == 1) {
            Link.create(confPAKDD(0).id, "http://www.informatik.uni-trier.de/~ley/db/conf/pakdd/index.html")
        }
        
        Ok("Data Created, but you better check...")
    }
    
    def index() = Action { implicit request =>
        Ok(views.html.index(Conference.count(), searchForm))
    }
    
    def conference(id: Long) = Action { implicit request =>
        // Retrieve the conference
        val conference = Conference.getById(id)
        
        if (conference.isDefined) {
            val links = Link.getByConferenceId(conference.get.id)
            val comments = Comment.getByConferenceId(conference.get.id)
            Ok(views.html.conference(conference.get, links, comments))
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
		            Ok(views.html.noresults(searchForm, query, Category.all))
		        } else {
		            // Display the list of all matching conferences
		            val matchingExactly = conferences.filter { conference => 
		                conference.name.equalsIgnoreCase(query) ||
		                conference.shortName.getOrElse("").equalsIgnoreCase(query) 
	                }
		            if(matchingExactly.size == 1) {
		                Redirect(routes.Application.conference(matchingExactly(0).id))
		            } else {
		                Ok(views.html.results(searchForm, query, conferences, Category.all))
//		            	Ok(views.html.index(0, searchForm))
		            }
		        }
            }
        )
    }
    
    // -- Javascript routing
    
    /*
    def javascriptRoutes = Action {
    	import routes.javascript._
    	Ok(Routes.javascriptRouter("jsRoutes")(
    			controllers.routes.javascript.Application.addUrl
		)).as("text/javascript") 
  	}
  	*/
//
//    def newTask = Action { implicit request =>
//        taskForm.bindFromRequest.fold(
//            errors => BadRequest(views.html.index(Task.all(), errors)),
//            label => {
//                Task.create(label)
//                Redirect(routes.Application.tasks)
//            })
//    }
//
//    def deleteTask(id: Long) = Action {
//        Task.delete(id)
//        Redirect(routes.Application.tasks)
//    }

//    val taskForm = Form("label" -> nonEmptyText)
}


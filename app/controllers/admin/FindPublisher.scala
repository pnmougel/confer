package controllers.admin

import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap

import models.Comment
import models.Conference
import models.ConferenceRelation
import models.Link
import models.Publisher
import models.SubField
import play.api.data.Forms._
import play.api.mvc._
import play.api._

/**
 * Merge conference with similar short name
 */
object FindPublisher extends Controller {
	
    def index = Action {
        
        var publishers = new HashMap[String, Long]()
        Publisher.all.foreach { publisher =>
            publishers(publisher.name.toLowerCase()) = publisher.id
        }
        
        Conference.all().foreach { conference =>
            val name = conference.name.toLowerCase()
            publishers.foreach { case (publisherName, publisherId) =>
                
                if(name.contains(publisherName)) {
                    Conference.updatePublisher(conference.id, publisherId)
                    
                    // Update the name to remove the publisher
                    val newName = conference.name.replaceAll("(?i)" + publisherName, "").replaceAll("  ", " ").trim
                    Conference.updateName(conference.id, newName)
                }
            }
        }
        
        Ok("Done finding publishers")
    }       
}
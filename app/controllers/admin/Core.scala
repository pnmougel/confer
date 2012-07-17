package controllers.admin

import scala.collection.JavaConversions._
import org.jsoup.nodes.Document
import models.Conference
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api._
import scala.collection.mutable.ListBuffer
import models.ExternalScore

object Core extends Controller {
	
    val coreRankingId = 2
    
    val confMatchingForm = Form (
    	tuple("score" -> list(text), 
    	        "shortName" -> list(text), 
    	        "longName" -> list(text),
    	        "matchingId" -> list(number))
    	
    )
    
    def update() = Action { implicit request =>
        confMatchingForm.bindFromRequest.fold(
            errors => {
                println("Argh")
                BadRequest("Unable to create")
            },
            params => {
                
                val scores = params._1
                val shortNames = params._2
                val longNames = params._3
                val matchingIds = params._4
                
                println(scores.size)
                
                var idx = 0
                while(idx < scores.size) {
	                val score = scores(idx) match {
	                    case "A*" => 4
	                    case "A" => 3
	                    case "B" => 2
	                    case "C" => 1
	                    case _ => -1
	                }
                    
	                val conferenceId = {
	                    if(matchingIds(idx) == -1) {
	                        Conference.create(longNames(idx), shortNames(idx), 1, 261)
	                    } else {
	                        matchingIds(idx)
	                    }
	                }
	                
	                setScore(conferenceId, score)
                    idx += 1
                }
                Ok
            }
        )
    }
    
    def setScore(conferenceId : Long, score : Int) = {
        if(!ExternalScore.getRankingForConference(conferenceId, coreRankingId).isDefined) {
            ExternalScore.create(conferenceId, coreRankingId, score)
        }
    }
    
    def index = Action { implicit request =>
        ExternalScore.clearCore()
        var matches = new ListBuffer[ConferenceMatch]
        
        var nbRow = 0
        
        val doc = Utils.getDocument("core", "core", "http://www-lipn.univ-paris13.fr/~bennani/CSRank.html")
        val rows = doc.select("tr")
        rows.foreach { row =>
            val tds = row.select("td")
            if(tds.size() == 3) {
                
                if(nbRow < 999999999) {
                val it = tds.iterator()
                val shortName = it.next().text().replaceAll("""\\""", "").replace("(", "").replace(")", "")
	            val longName = it.next().text().replaceAll("""\\""", "").replace("(", "").replace(")", "")
	            val rank = it.next().text()
	            
	            val score = rank match {
                    case "A*" => 4
                    case "A" => 3
                    case "B" => 2
                    case "C" => 1
                    case _ => -1
                }
	            
	            println(rank + " / " + shortName + " / " + longName)
	            
                val confList = Conference.findByShortName(shortName)
                if(confList.size == 1) {
                    setScore(confList(0).id, score)
                	// val possibleMatches = confList.map( conf => (conf.id, conf.name)).toList
                	// matches += ConferenceMatch(shortName, longName, possibleMatches, rank)
                } else {
                    val confList2 = Conference.findByName(longName)
                    if(confList2.size == 1) {
                        setScore(confList2(0).id, score)
                       // val possibleMatches = confList2.map( conf => (conf.id, conf.name)).toList
                       // matches += ConferenceMatch(shortName, longName, possibleMatches, rank)
                    } else {
                        val confList3 = Conference.findByNameAndShortName(longName)
                        if(confList3.size == 1) {
                            setScore(confList3(0).id, score)
                        	// val possibleMatches = confList3.map( conf => (conf.id, conf.name)).toList
                            // matches += ConferenceMatch(shortName, longName, possibleMatches, rank)
                        } else {
                            val possibleMatches = confList3.map( conf => (conf.id, conf.name)).toList
                            matches += ConferenceMatch(shortName, longName, possibleMatches, rank)
                        }
                    }
                }
                nbRow += 1
                }
            }
            
        }
        Ok(views.html.admin.conferenceMatch(matches.toList))
    }
}
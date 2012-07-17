package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Conference
import models.Publisher
import models.Link
import scala.collection.mutable.Stack

object Ranking extends Controller {
    
    val nbConferencesPerPage = 10
    
    val paginateConferences = Form(
        tuple(
            "pageNum" -> number,
            "field" -> number,
            "nat" -> number,
            "intl" -> number,
            "conference" -> number,
            "journal" -> number,
            "workshop" -> number,
            "subfields" -> text,
            "orderBy" -> text,
            "sort" -> text))
            
    def byField(fieldId : Long) = Action { implicit request =>
        val conferences = Conference.findByField(fieldId)
        // val conferences = Conference.findByShortName("VLDB")
        val subFields = models.SubField.getByField(fieldId)
        // val conferences = Conference.getFirstPage(fieldId)
        Ok(views.html.ranking(fieldId, conferences, subFields, 1, 10))
    }
    
    def page() = Action { implicit request =>
        paginateConferences.bindFromRequest.fold(
            errors => BadRequest("Unable to get the page"),
            params => {
                val subFieldsList = params._8.split(",")
                val selectedSubFields : List[Long] = if(params._8.size > 0) {
                	subFieldsList.map(_.split("_")(1).toLong).toList
                } else {
                    List[Long]()
                }
                val cTypes = getCTypes(params._3 == 1, params._4 == 1, params._5 == 1, params._6 == 1, params._7 == 1)
                val fieldId = params._2
                val nbItems = Conference.countPages(fieldId, cTypes, selectedSubFields)
                val nbPages = scala.math.max(1, scala.math.floor((nbItems  + nbConferencesPerPage - 1) / nbConferencesPerPage)).toInt
                val pageNum = params._1
                val orderBy = params._9 match {
                    case "user" => "user_score"
                    case "ext" => "external_score"
                    case "avg" => "user_score"
                    case _ => "user_score"
                }
                val sort = params._10
                val startAt = if(pageNum > nbPages) {
                    0
                } else {
                    (pageNum - 1) * nbConferencesPerPage
                }
                val conferences = Conference.getPage(fieldId, cTypes, selectedSubFields, nbConferencesPerPage, startAt, orderBy, sort)
                Ok(views.html.rankingConferences(conferences, startAt + 1, pageNum, nbPages))
            }
        )
    }
    
    def getCTypes(isNational : Boolean, isInternational : Boolean, 
            isConference : Boolean, isJournal : Boolean, isWorkshop : Boolean) : List[Long] = {
        var cTypes = new Stack[Long]()
        
        if(isConference) {
            if(isInternational) {
                cTypes.push(1)
            }
            if(isNational) {
            	cTypes.push(4)
            }
        }
        if(isJournal) {
            if(isInternational) {
                cTypes.push(2)
            }
            if(isNational) {
            	cTypes.push(5)
            }
        }
        if(isWorkshop) {
            if(isInternational) {
                cTypes.push(3)
            }
            if(isNational) {
            	cTypes.push(6)
            }
        }
        cTypes.toList
    }
}
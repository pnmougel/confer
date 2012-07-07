package controllers.admin

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import controllers.package$
import models.Conference
import java.io.PrintWriter
import org.jsoup.Jsoup
import scala.collection.mutable.HashMap
import java.io.File
import scala.io.Source
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashSet
import java.util.Date

object MicrosoftAR extends Controller {
	
    val Journal = 4
    val Conference = 3
    
    def index = Action {
        models.Field.deleteAll()
        models.SubField.deleteAll()
        models.Link.deleteAll()
        models.Conference.deleteAll()
        
        
        getByEntity(Conference)
        getByEntity(Journal)
    	

    	if(subDomainsList.size != subDomainsSet.size) {
            println("ERROR: Several subdomains share the same name, there is a conflict. Use the domainId to avoid it.")
        }
        
        Ok("Data inserted :)")
    }
    
    def getDoc(fileName : String, url : String) : Document = {
//        println(url)
        var cacheFile = new File("cache/microsoft_AR/" + fileName)
        if(cacheFile.exists()) {
            val html = Source.fromFile(cacheFile).getLines().mkString("\n")
            Jsoup.parse(html)
        } else {
            println(url)
            val tmpDoc = Jsoup.connect(url).timeout(200000).get()
            val htmlFile = new PrintWriter(cacheFile)
            htmlFile.write(tmpDoc.body().toString())
            htmlFile.flush()
            htmlFile.close()
            tmpDoc
        }
    }
    
    def getTopDomains(entityType : Int) : HashMap[Int, String] = {
        val fileName = "subDomain_" + entityType
        val doc = getDoc(fileName, "http://academic.research.microsoft.com/RankList?entitytype=" + entityType + 
	                "&topdomainid=2&subdomainid=0&last=0")
        
        val topDomains = doc.select("div.option")
        var map = new HashMap[Int, String]()
        topDomains.foreach { topDomain =>
            map(topDomain.select("input").first().attr("value").toInt) = topDomain.select("span").first().text()
        }
        map
    }
    
    var subDomainsSet = new HashSet[String]()
    var subDomainsList = new ListBuffer[String]()
    
    def getSubDomains(entityType : Int, topDomain : Int) : HashMap[Int, String] = {
        val fileName = "subDomain_" + entityType + "_topDomain" + topDomain
        val doc = getDoc(fileName, "http://academic.research.microsoft.com/RankList?entitytype=" + entityType + 
	                "&topdomainid=" + topDomain + "&subdomainid=0&last=0")
	    var found = false
	    var map = new HashMap[Int, String]()
	    doc.toString().split("\n").foreach { line =>
            if(line.contains("options[") && line.contains("] = {")) {
                if(!found) {
                    found = true
	                line.split(";").foreach { elem => 
	                    val subFieldName = elem.split("\"")(3)
	                    val subFieldId = elem.split("\"")(7)
	                    
	                    subDomainsSet.add(subFieldName)
	                    subDomainsList.add(subFieldName)
	                    // Handle special cases
	                    if(subFieldName == "Last 5 Years") {
	                        map(0) = "Multidisciplinary"
	                    } else {
	                        if(subFieldId != "0") {
		                    	map(subFieldId.toInt) = subFieldName
		                    }
	                    }
	                }
                }
            }
        }
        map
    }
    
    def getItems(entityType : Int, topDomain : Int, subDomain : Int) : Unit = {
        val fileName = "items_type" + entityType + "_topDomain" +  topDomain + "_subDomain" + subDomain
        val doc = getDoc(fileName, "http://academic.research.microsoft.com/RankList?entitytype=" + entityType + 
	                "&topdomainid=" + topDomain + "&subdomainid=" + subDomain + "&last=0")
        val results = doc.select("span.result").text()
        if(results.split(" ").size < 5) {
            return
        }
        val nbResults = results.replaceAll(",", "").split(" ")(4).toInt
        
        var startPage = 1
        var resultCount = 0
        while(startPage < nbResults + 1) {
            val page = getItemsByPage(entityType, topDomain, subDomain, startPage)
            resultCount += page.size
            startPage += 100
        }
        
        if(resultCount != nbResults) {
            println("Found " + resultCount + " while " + nbResults + " where expected")
            println("http://academic.research.microsoft.com/RankList?entitytype=" + entityType + 
	                "&topdomainid=" + topDomain + "&subdomainid=" + subDomain + "&last=0")
        }
    }
    
    def getItemsByPage(entityType : Int, topDomain : Int, subDomain : Int, startPage : Int) : 
    	List[(String, String, String, String)] = {
        val fileName = "itemsPage_type" + entityType + "_topDomain" +  topDomain + "_subDomain" + subDomain + "_start" + startPage
        val doc = getDoc(fileName, "http://academic.research.microsoft.com/RankList?entitytype=" + entityType + 
	                "&topdomainid=" + topDomain + "&subdomainid=" + subDomain + "&last=0&start=" + 
	                startPage + "&end=" + (startPage + 99))
	    doc.select("tr").map { tr =>
            val url = "http://academic.research.microsoft.com" + tr.select("td.rank-content").select("a").attr("href")
            val name = tr.select("td.rank-content").select("a").text()
            val (shortName, fullName) = {
            	if(name.split(" - ").size != 2) {
            	    (("", name))
            	} else {
            	    ((name.split(" - ")(0), name.split(" - ")(1)))
            	}
            }
            val data = tr.select("td.staticOrderCol").text().split(" ")
            val (nbPublications, hIndex) = {
                if(data.size == 2) {
                    (data(0), data(1))
                } else {
                    ("-1", "-1")
                }
            }
            if(data.size == 2) {
                val conferenceId = models.Conference.create(
                    fullName, shortName, false, false, (entityType == Journal), curSubFieldId)
                    models.Link.create(conferenceId, url, "Microsoft AR", new Date())
            }
            
                    
            ((name, url, nbPublications, hIndex))
        }.filter( a => a._1 != "").toList
    }
    
    var curFieldId : Long = 0
    var curSubFieldId : Long = 0
    
    def getByEntity(entity: Int) = {
        getTopDomains(entity).foreach { field =>
            // Add field into the table
            
            val fieldId = field._1
            val fieldName = field._2
            
            curFieldId = {
                if(!models.Field.getByName(fieldName).isDefined) {
                    models.Field.create(fieldName)
                } else {
                    models.Field.getByName(fieldName).get.id
                }
            }
            
            val map = getSubDomains(entity, field._1)
            map.foreach { subField =>
                // Add sub field into the table
            	val subFieldId = subField._1
            	val subFieldName = subField._2
            	
            	curSubFieldId = {
	                if(!models.SubField.getByName(subFieldName).isDefined) {
	                    models.SubField.create(subFieldName, curFieldId)
	                } else {
	                	models.SubField.getByName(subFieldName).get.id
	                }
	            }
            	
            	getItems(entity, fieldId, subFieldId)
            }
        }
    }
}
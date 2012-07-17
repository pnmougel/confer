package controllers.admin
import java.io.PrintWriter
import org.jsoup.Jsoup
import java.io.File
import scala.io.Source
import org.jsoup.nodes.Document

object Utils {
	def getDocument(directory : String, fileName : String, url : String) : Document = {
//        println(url)
	    val dirFile = new File("cache/" + directory)
	    dirFile.mkdirs()
	    
        val cacheFile = new File("cache/" + directory + "/" + fileName)
        if(cacheFile.exists()) {
            val html = Source.fromFile(cacheFile).getLines().mkString("\n")
            Jsoup.parse(html)
        } else {
            val tmpDoc = Jsoup.connect(url).timeout(200000).get()
            val htmlFile = new PrintWriter(cacheFile)
            htmlFile.write(tmpDoc.body().toString())
            htmlFile.flush()
            htmlFile.close()
            tmpDoc
        }
    }
}
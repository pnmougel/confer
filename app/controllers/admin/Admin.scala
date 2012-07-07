package controllers.admin

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import controllers.package$
import models.Conference

object Admin extends Controller {
	
    def index = Action {
    	Ok("Index")
        // Redirect(routes.Admin.list)
    }
    
    def list = Action {
        var methodNames = ""
        Conference.getClass.getMethods foreach { method =>
            methodNames += method + " - " + method.getParameterTypes.size + "\n"
            method.getParameterTypes.foreach { p =>
                methodNames += "\t" + p.getName + "\n"
            }
            method.getParameterAnnotations.foreach { m =>
                m.foreach { x =>
                    methodNames += "\t" + x.toString + "\n"
                }
            }
        }
        Ok(methodNames)
        
    }
}
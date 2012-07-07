package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Conference
import models.Publisher
import models.Link

object User extends Controller {
	val userForm = Form(tuple("email" -> text, "password" -> text))
    
    def login = Action { implicit request =>
        userForm.bindFromRequest.fold(
            errors => BadRequest("Unable to login, check that you typed a valid email address"),
            user => {
                
                val loggedUser = models.User.authenticate(user._1, user._2)
                if(loggedUser.isDefined) {
                    Ok("You successfully logged in").withSession("userId" -> loggedUser.get.id.toString())
                } else {
                    BadRequest("Unable to login, the information you specified are incorrect")
                }
            }
        )
    }
	
	def logout = Action { implicit request =>
	    Ok("You logged out").withNewSession
    }
	
	def add = Action { implicit request =>
        userForm.bindFromRequest.fold(
            errors => BadRequest("Unable to create an account, check that you typed a valid email address"),
            user => {
                // Create a new user if not existing
                if(!models.User.findByEmail(user._1).isDefined) {
                    val pseudo = user._1.split("@")(0)
                    models.User.create(user._1, user._2, pseudo)
                    val loggedUser = models.User.findByEmail(user._1)
                    Ok("Account succesfully created!").withSession("userId" -> loggedUser.get.id.toString())
                } else {
                    BadRequest("Sorry, this email already corresponds to a user")
                }
            }
        )
    }
}
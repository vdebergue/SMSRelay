package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._
import play.api.libs.functional.syntax._
/** Uncomment the following lines as needed **/
/**
import play.api.Play.current
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import java.util.concurrent._
import scala.concurrent.stm._
import akka.util.duration._
import play.api.cache._
import play.api.libs.json._
**/

object Authentification extends Controller {

  implicit val rds = (
    (__ \ "login").read[String] and
    (__ \ "password").read[String]
  ) tupled

  def login = Action(parse.json) { request =>
    request.body.validate[(String, String)].map{
      case (login, password) => {
        Logger.info(s"$login wants to login")
        val regOption : Option[String] = models.Registration.findId(login, password)
        regOption match {
          case Some(registrationId) => Ok(Json.obj("registrationId" -> registrationId))
          case None => BadRequest
        }
      }
    }.recoverTotal{
      e => BadRequest
    }
  }

}
package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
// you need this import to have combinators
import play.api.libs.functional.syntax._

object Registration extends Controller {

  implicit val rds = (
    (__ \ "registrationId").read[String] and
    (__ \ "login").read[String] and
    (__ \ "password").read[String]
  ) tupled

  def index = Action(parse.json) { request =>
    request.body.validate[(String, String, String)].map {
      case (registrationId, login, password) => {
        // TODO store registrationId with login and passwd
        models.Registration.store(registrationId, login, password)
        Logger.info(s"Registration - Login: $login - password: $password, $registrationId")
        Ok("Nice ! " + registrationId + "\n")
      }
    }.recoverTotal{
      e => BadRequest("Erreur: " + JsError.toFlatJson(e))
    }
  }
}
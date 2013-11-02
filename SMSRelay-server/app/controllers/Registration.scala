package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
// you need this import to have combinators
import play.api.libs.functional.syntax._

object Registration extends Controller {

  implicit val rds = (
    (__ \ "registrationId").read[String]
  )

  def index = Action(parse.json) { request =>
    request.body.validate[(String)].map {
      case (registrationId) => {
        // TODO store registrationId
        Logger.info("Registration " + registrationId)
        Ok("Nice ! " + registrationId + "\n")
      }
    }.recoverTotal{
      e => BadRequest("Erreur: " + JsError.toFlatJson(e))
    }
  }
}
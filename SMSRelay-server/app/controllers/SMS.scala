package controllers

import play.api._
import play.api.mvc._
import play.api.data.validation._
import play.api.libs._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee._

import utils.Conf
/** Uncomment the following lines as needed **/
/**
import play.api.Play.current
import play.api.libs._
import java.util.concurrent._
import scala.concurrent.stm._
import akka.util.duration._
import play.api.cache._
**/

object SMS extends Controller {

  val (out, in) = Concurrent.broadcast[JsValue]

  val smsFromPhoneValidation = __.read[JsObject] keepAnd(
    (__ \ "type").read[String]
  ).filter(ValidationError("Wrong type"))(t => t == "smsFromPhone")


  def smsFromPhone = Action(parse.json) { request =>
    val json : JsValue = request.body
    json.validate(smsFromPhoneValidation).map { p =>
      Logger.debug(Json.prettyPrint(p))
      in.push(p)
    }
    Ok
  }

  implicit val rds: Reads[(String, String)] = (
    (__ \ "text").read[String] and
    (__ \ "phoneNumber").read[String]
  ) tupled

  def send = Action(parse.json) { request =>
    request.body.validate[(String, String)](rds).map {
      case (smsText, phoneNumber) => {
        val registrationId = "APA91bE1EtwhXWBrGS9szkCPj-dEbsR5q8bawrnZ_8pujARteWEZpqAnd93oVYpd-RDj8jdMOpaRDA4heE_CqUmX9bTVhL2btQonXiGIeCcwARUu-cdGMu40JKGiDVYi-EwIu_67EW97dN7QOOM92TP1ePI1Bk_rQA"
        val json = Json.obj(
          "registration_ids" -> Json.arr(registrationId),
          "time_to_live" -> 60*10, // 10mns
          "data" -> Json.obj(
            "type" -> "smsToPhone",
            "text" -> smsText,
            "number" -> phoneNumber
          )
        )
        WS.url(Conf.gcmUrl).withHeaders("Authorization" -> ("key=" + Conf.gcmApiKey)).post(json).map{
          response => Logger.debug(s"Status ${response.status} \n${response.body}")
        }
        Ok(Json.obj("message" -> "Sending ..."))
      }
    }.recoverTotal{
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }

  val jsTransform = (__ \ "registrationId").json.prune

  val filterValues: Enumeratee[JsValue, JsValue] = Enumeratee.map[JsValue]{ json =>
    json.transform(jsTransform).map{
      p => p
    }.recoverTotal( _ => Json.obj())
  }

  def smsFeed = Action {
    Ok.chunked(out &> filterValues &> EventSource()).as("text/event-stream")
  }
}
package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.db._

case class Registration(id: Option[Int], login: String, password: String, registrationId: String)

object Registrations extends Table[Registration]("registration") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def password = column[String]("password")
  def registrationId = column[String]("registrationId")

  def * = id.? ~ login ~ password ~ registrationId <> (Registration.apply _, Registration.unapply _)

}

object Registration {

  def store(regId: String, login: String, password: String) : Unit = {
    DB.withSession{ implicit session : Session =>
      Registrations.insert(Registration(None, login, password, regId))
    }
  }

  def findId(login: String, password: String) : Option[String] = {
    DB.withSession{ implicit session =>
      Query(Registrations).filter((r) => r.login === login && r.password === password).firstOption.map(_.registrationId)
    }
  }
}
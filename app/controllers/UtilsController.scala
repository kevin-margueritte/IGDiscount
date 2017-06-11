package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SellerCompany, SimpleUser, Token}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Controller which contains util functions
  */
@Singleton
class UtilsController @Inject() extends Controller {

  /**
    * JSon : Simple User
    */
  val kindOfSimpleUser = Json.obj(
    "kindOfUser" -> "Simple User"
  )

  /**
    * JSon : Seller Company
    */
  val kindOfSellerCompany = Json.obj(
    "kindOfUser" -> "Seller Company"
  )

  /**
    * JSon : Admin
    */
  val kindOfAdmin = Json.obj(
    "kindOfUser" -> "Admin"
  )

  /**
    * JSon : Your session is expired
    */
  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  /**
    * JSon : Authentification required
    */
  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  /**
    * Kind of user connected
    * @return Kind of user connected
    */
  def kindOfUser = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => Ok(kindOfAdmin.deepMerge(Json.obj("id"-> a.id)))
          case su : SimpleUser => Ok(kindOfSimpleUser.deepMerge(Json.obj("id"-> su.id)))
          case sc : SellerCompany => Ok(kindOfSellerCompany.deepMerge(Json.obj("id"-> sc.id)))
        }
        case false => Unauthorized(jsonTokenExpired)
      }
      case _ => Forbidden(jsonNoToken)
    }
  }

}

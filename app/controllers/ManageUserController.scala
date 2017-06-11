package controllers

import javax.inject.{Inject, Singleton}

import models.{Admin, SellerCompany, SimpleUser, Token}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Controller to manage users
  */
@Singleton
class ManageUserController @Inject() extends Controller {

  /**
    * JSon : Authentification required
    */
  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  /**
    * JSon : You cannot the permission
    */
  val jsonRequiredAdmin = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  /**
    * JSon : Your session is expired
    */
  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  /**
    * JSon : Parameter error
    */
  val jsonErrorParameter = Json.obj(
    "error" -> true,
    "message" -> "Parameter error"
  )

  /**
    * JSon : User deleted
    */
  val jsonUserDeleted = Json.obj(
    "error" -> false,
    "message" -> "User deleted"
  )

  /**
    * JSon : User not deleted
    */
  val jsonUserNoDeleted = Json.obj(
    "error" -> true,
    "message" -> "User no deleted"
  )

  /**
    * JSon : User updated
    */
  val jsonUserUpdated = Json.obj(
    "error" -> false,
    "message" -> "User updated"
  )

  /**
    * JSon : Email already exists
    */
  val jsonErrorEmail = Json.obj(
    "error" -> true,
    "message" -> "Email already exists"
  )

  /**
    * Take "email" -> email
    *  "postalCode" -> nonEmptyText
    * "street" -> nonEmptyText
    * "city" -> nonEmptyText
    * "streetNumber" -> nonEmptyText
    * "firstName" -> nonEmptyTex,
    * "lastName" -> nonEmptyText
    */
  val parameterSimpleUserWithoutPassword = Form(
    tuple(
      "email" -> email,
      "postalCode" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  /**
    * Take "email" -> email
    * "postalCode" -> nonEmptyText
    * "street" -> nonEmptyText
    * "city" -> nonEmptyText
    * "streetNumber" -> nonEmptyText
    * "siret" -> nonEmptyText
    * "companyName" -> nonEmptyText
    */
  val parameterSellerCompanyWithoutPassword = Form(
    tuple(
      "email" -> email,
      "postalCode" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "streetNumber" -> nonEmptyText,
      "siret" -> nonEmptyText,
      "companyName" -> nonEmptyText
    )
  )

  /**
    * Take "email" -> email,
    * "firstName" -> nonEmptyText
    * "lastName" -> nonEmptyText
    */
  val parameterAdminWithoutPassword = Form(
    tuple(
      "email" -> email,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )
  )

  /**
    * Take "password" -> nonEmptyText(minLength = 5)
    */
  val parameterChangePassword = Form(
    "password" -> nonEmptyText(minLength = 5)
  )

  /**
    * Get information of a simple user, user must be connected or an admin connected
    * @param id ID SimpleUser
    * @return SimpleUser information or HTTP error code
    */
  def getSimpleUser(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SimpleUser.find(id)))
          case su : SimpleUser => SimpleUser.tokenConform(c.value, SimpleUser.find(id).getOrElse(new SimpleUser())) match {
            case true => Ok(Json.toJson(SimpleUser.find(id)))
            case false => Forbidden(jsonRequiredAdmin)
          }
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get information of a seller company, seller must be connected or an admin connected
    * @param id ID SellerCompany
    * @return SellerCompany information or HTTP error code
    */
  def getSellerCompany(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SellerCompany.find(id)))
          case sc : SellerCompany => SellerCompany.tokenConform(c.value, SellerCompany.find(id).getOrElse(new SellerCompany())) match {
            case true => Ok(Json.toJson(SellerCompany.find(id)))
            case false => Forbidden(jsonRequiredAdmin)
          }
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get information of a admin connected
    * @param id ID Admin
    * @return Admin information or HTTP error code
    */
  def getAdmin(id : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(Admin.find(id)))
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all information of a simple user, an admin must be connnected
    * @return List SimpleUser or HTTP error code
    */
  def getAllSimpleUser = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SimpleUser.findAll()))
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all information of a seller company, an admin must be connnected
    * @return List SellerCompany or HTTP error code
    */
  def getAllSellerCompany = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(SellerCompany.findAll()))
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all information of a admin, an admin must be connnected
    * @return List Admin or HTTP error code
    */
  def getAllAdmin = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => Ok(Json.toJson(Admin.findAll()))
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Delete simple user, an admin must be connnected
    * @param id ID SimpleUser
    * @return HTTP code
    */
  def deleteSimpleUser(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            SimpleUser.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Delete seller company, an admin must be connnected
    * @param id ID SellerCompany
    * @return HTTP code
    */
  def deleteSellerCompany(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            SellerCompany.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Delete admin, an admin must be connnected
    * @param id ID Admin
    * @return HTTP code
    */
  def deleteAdmin(id: Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a: Admin => {
            Admin.deleteById(id) match {
              case true => Ok(jsonUserDeleted)
              case false => NotFound(jsonUserNoDeleted)
            }
          }
          case _ => Forbidden(jsonRequiredAdmin)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Update simple user without password, the user must be connected or an admin connnected
    * @param id ID SimpleUser
    * @return HTTP code
    */
  def updateSimpleUserWithoutPassword(id : Long) = Action { implicit request =>
    parameterSimpleUserWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, postalCode, street, city, streetNumber, firstName, lastName) = formData
        val su = SimpleUser.find(id).get
        su.email = email
        su.postalCode = postalCode
        su.street = street
        su.city = city
        su.firstName
        su.lastName = lastName
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SimpleUser.updateWithoutPassword(su) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    SimpleUser.updateWithoutPassword(su) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Forbidden(jsonRequiredAdmin)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }

  /**
    * Update seller company without password, the seller must be connected or an admin connnected
    * @param id ID SellerCompany
    * @return HTTP code
    */
  def updateSellerCompanyWithoutPassword(id : Long) = Action { implicit request =>
    parameterSellerCompanyWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, postalCode, street, city, streetNumber, siret, companyName) = formData
        val sc = SellerCompany(id, email, "", postalCode, street, city, streetNumber, siret, companyName)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SellerCompany.updateWithoutPassword(sc) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SellerCompany => {
                SellerCompany.tokenConform(c.value, u) match {
                  case true => {
                    SellerCompany.updateWithoutPassword(sc) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Forbidden(jsonRequiredAdmin)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }

  /**
    * Update admin without password, an admin must be connnected
    * @param id ID Admin
    * @return HTTP code
    */
  def updateAdminWithoutPassword(id : Long) = Action { implicit request =>
    parameterAdminWithoutPassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (email, firstName, lastName) = formData
        val a = Admin(id, email, "", firstName, lastName)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case x : Admin => {
                Admin.updateWithoutPassword(a) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }

  /**
    * Update simple' password, the user must be connected or an admin connnected
    * @param id ID SimpleUser
    * @return HTTP code
    */
  def updateSimpleUserPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val su = SimpleUser(id, password, null , null, null, null, null, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SimpleUser.updatePassword(su) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SimpleUser => {
                SimpleUser.tokenConform(c.value, u) match {
                  case true => {
                    SimpleUser.updatePassword(su) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Forbidden(jsonRequiredAdmin)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }

  /**
    * Update seller company's password, the seller must be connected or an admin connnected
    * @param id ID SellerCompany
    * @return HTTP code
    */
  def updateSellerCompanyPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val sc = SellerCompany(id, null, password, null, null, null, null, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case a : Admin => {
                SellerCompany.updatePassword(sc) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case u : SellerCompany => {
                SellerCompany.tokenConform(c.value, u) match {
                  case true => {
                    SellerCompany.updatePassword(sc) match {
                      case true => Ok(jsonUserUpdated)
                      case _ => Conflict(jsonErrorEmail)
                    }
                  }
                  case false => Forbidden(jsonRequiredAdmin)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }
4/**
    * Update admin's password, an admin must be connnected
    * @param id ID Admin
    * @return HTTP code
    */
  def updateAdminPassword(id : Long) = Action { implicit request =>
    parameterChangePassword.bindFromRequest.fold(
      formWithErrors => BadRequest(jsonErrorParameter),
      formData => {
        val (password) = formData
        val a = Admin(id, null, password, null, null)
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case x : Admin => {
                Admin.updatePassword(a) match {
                  case true => Ok(jsonUserUpdated)
                  case _ => Conflict(jsonErrorEmail)
                }
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    )
  }

}

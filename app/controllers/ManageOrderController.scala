package controllers

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}

import models.{SellerCompany, _}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Controller to manage orders
  */
@Singleton
class ManageOrderController @Inject() extends Controller {

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
  val jsonRequiredOwner = Json.obj(
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
    * JSon :Order created
    */
  val jsonOrderCreated = Json.obj(
    "error" -> false,
    "message" -> "Order created"
  )

  /**
    * JSon : Insufficient stock
    */
  val jsonInsufficientStock = Json.obj(
    "error" -> true,
    "message" -> "Insufficient stock"
  )

  /**
    * JSon : Order updated
    */
  val jsonOrderUpdated = Json.obj(
    "error" -> false,
    "message" -> "Order updated"
  )

  /**
    * JSon : Order paid
    */
  val jsonOrderPaid = Json.obj(
    "error" -> false,
    "message" -> "Order paid"
  )

  /**
    * JSon : Order not confirmed by seller
    */
  val jsonOrderNotConfirmed = Json.obj(
    "error" -> false,
    "message" -> "Order not confirmed by seller"
  )

  /**
    * Create an order of a simple user connected
    * @param idSimpleUser ID simple user
    * @return HTTP code
    */
  def createOrder(idSimpleUser : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u : SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                BasketRow.findAllBasket(u).flatMap(br =>
                  if (br.quantity > br.product.quantity) br.product.name
                  else {
                    br.delete()
                    Order(br.quantity, OrderState.PENDING, br.product.price, br.product, br.simpleUser).save()
                    "ok"
                  }
                )
                Created(jsonOrderCreated)
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all orders of a simple user connected
    * @param idSimpleUser ID simple user
    * @return List orders or HTTP error code
    */
  def getOrderSimpleUser(idSimpleUser : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case u : SimpleUser => {
            SimpleUser.tokenConform(c.value, u) match {
              case true => {
                Ok(Json.toJson(Order.findAll()))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all orders of a seller company connected
    * @param idSellerCompany ID seller company
    * @return List orders or HTTP error code
    */
  def getOrderSellerCompany(idSellerCompany : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                Ok(Json.toJson(Order.findAll(sc)))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all orders with state 'Cancelled or Paid' of a seller company connected
    * @param idSellerCompany ID seller company
    * @return List orders or HTTP error code
    */
  def getOrderSellerCompanyCancelledOrPaid(idSellerCompany : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                Ok(Json.toJson(Order.findAllCancelledOrPaid(sc)))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all orders with state 'Pending or Paid' of a seller company connected
    * @param idSellerCompany ID seller company
    * @return List orders or HTTP error code
    */
  def getOrderSellerCompanyPendingOrPaid(idSellerCompany : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                Ok(Json.toJson(Order.findAllPendingOrPaid(sc)))
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Update product state into 'Confirmed by seller' of a seller company
    * @param idSimpleUser ID simple user
    * @param idOrder ID order
    * @return HTTP code
    */
  def updateOrderConfirm(idSimpleUser : Long, idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                val o = Order.find(idOrder).get
                if (o.state == OrderState.PENDING && o.quantity <= o.product.quantity) {
                  o.state = OrderState.CONFIRMED_BY_SELLER
                  o.product.quantity -= o.quantity
                  o.product.update()
                  o.stateDate = new Timestamp(new Date().getTime)
                  o.update()
                  Ok(jsonOrderUpdated)
                }
                else {
                  Conflict(jsonInsufficientStock)
                }
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Get all orders confirmed, an admin authentificated is required
    * @return List orders or HTTP error code
    */
  def getOrderConfirmed = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => {
            Ok(Json.toJson(Order.findAllConfirmed()))
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Update product state into 'Paid', an admin authentificated is required
    * @param idOrder ID order
    * @return HTTP code
    */
  def updateOrderPaid(idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case a : Admin => {
            val o = Order.find(idOrder).get
            if (o.state == OrderState.CONFIRMED_BY_SELLER) {
              o.state = OrderState.PAID
              o.stateDate = new Timestamp(new Date().getTime)
              o.update()
              Ok(jsonOrderPaid)
            }
            else {
              Conflict(jsonOrderNotConfirmed)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Update product state into 'Cencelled by seller' of a seller company
    * @param idSimpleUser ID simple user
    * @param idOrder ID order
    * @return HTTP code
    */
  def updateOrderCancel(idSimpleUser : Long, idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                val o = Order.find(idOrder).get
                if (o.state == OrderState.PENDING) {
                  o.state = OrderState.CANCELLED_BY_SELLER
                  o.stateDate = new Timestamp(new Date().getTime)
                  o.update()
                  Ok(jsonOrderUpdated)
                }
                else {
                  Conflict(jsonInsufficientStock)
                }
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Update product state into 'Shipped' of a seller company
    * @param idSimpleUser ID simple user
    * @param idOrder ID order
    * @return HTTP code
    */
  def updateOrderShip(idSimpleUser : Long, idOrder : Long) = Action { implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case true => {
                val o = Order.find(idOrder).get
                if (o.state == OrderState.PAID) {
                  o.state = OrderState.SHIPPED
                  o.stateDate = new Timestamp(new Date().getTime)
                  o.update()
                  Ok(jsonOrderUpdated)
                }
                else {
                  Conflict(jsonInsufficientStock)
                }
              }
              case false => Forbidden(jsonRequiredOwner)
            }
          }
          case _ => Forbidden(jsonRequiredOwner)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

}

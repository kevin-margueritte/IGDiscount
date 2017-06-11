package controllers

import java.nio.file.Files
import javax.inject.{Inject, Singleton}

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by kevin on 05/11/16.
  */
@Singleton
class ManageProductController @Inject() extends Controller {

  /**
    * Take "description" -> nonEmptyText(minLength = 1, maxLength = 1000)
    * "name" -> nonEmptyText(minLength = 1, maxLength = 40)
    * "price" -> bigDecimal(precision=6,scale=2)
    * "quantity" -> number(min=0, max=10000)
    */
  val parameterCreateProduct = Form(
    tuple(
      "description" -> nonEmptyText(minLength = 1, maxLength = 1000),
      "name" -> nonEmptyText(minLength = 1, maxLength = 40),
      "price" -> bigDecimal(precision=6,scale=2),
      "quantity" -> number(min=0, max=10000)
    )
  )

  /**
    * Take "id" -> number
    */
  val parameterId = Form(
    "id" -> number
  )

  /**
    * Json : Parameters error
    */
  val jsonErrorForm = Json.obj(
    "error" -> true,
    "message" -> "Parameters error"
  )

  /**
    * Json : Authentification required
    */
  val jsonNoToken =Json.obj(
    "error" -> true,
    "message" -> "Authentification required"
  )

  /**
    * Json : Seller not found
    */
  val jsonNoSeller =Json.obj(
    "error" -> true,
    "message" -> "Seller not found"
  )

  /**
    * Json : You cannot the permission
    */
  val jsonPermission = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  /**
    * Json : Your session is expired
    */
  val jsonTokenExpired = Json.obj(
    "error" -> true,
    "message" -> "Your session is expired"
  )

  /**
    * Json : Image updated
    */
  val imageUpdated = Json.obj(
    "error" -> false,
    "message" -> "Image updated"
  )

  /**
    * Json : Product created
    */
  val jsonProductCreated = Json.obj(
    "error" -> false,
    "message" -> "Product created"
  )

  /**
    * Json : Product deleted
    */
  val jsonProductDeleted = Json.obj(
    "error" -> false,
    "message" -> "Product deleted"
  )

  /**
    * Json : You cannot the permission
    */
  val jsonRequiredAdmin = Json.obj(
    "error" -> true,
    "message" -> "You cannot the permission"
  )

  /**
    * Json : id
    */
  val jsonImageCreated = Json.obj(
    "error" -> true,
    "id" -> "id"
  )

  /**
    * Json : Product not found
    */
  val jsonProductNotFound = Json.obj(
    "error" -> true,
    "message" -> "Product not found"
  )

  /**
    * Json : Product updated
    */
  val jsonProductUpdated = Json.obj(
    "error" -> false,
    "message" -> "Product updated"
  )

  val imageMime = List("image/jpg", "image/png", "image/jpeg")

  /**
    * Get all products
    * @return List products
    */
  def allProducts = Action{implicit request =>
    Ok(Json.toJson(Product.findAll()))
  }

  /**
    * Get all available products
    * @return List available products
    */
  def allProductsByAvailable = Action{implicit request =>
    Ok(Json.toJson(Product.findAll(true)))
  }

  /**
    * Get all products of a seller company
    * @param id id seller company
    * @return List products
    */
  def allProductsBySeller(id: Long) = Action{implicit request =>
    SellerCompany.find(id) match {
      case None => NotFound(jsonNoSeller)
      case Some(s) => Ok(Json.toJson(Product.findBySeller(s)))
    }
  }

  /**
    * Get all available product of a seller company
    * @param id id seller company
    * @return List availabe products
    */
  def allProductsBySellerAndAvailable(id: Long) = Action{implicit request =>
    SellerCompany.find(id) match {
      case None => NotFound(jsonNoSeller)
      case Some(s) => Ok(Json.toJson(Product.findBySellerAvailability(s, true)))
    }
  }

  /**
    * Get all images
    * @return List images
    */
  def allImages = Action{implicit request =>
    Ok(Json.toJson(Image.findAll()))
  }

  /**
    * Get image
    * @param id ID image
    * @return Image
    */
  def image(id: Long) = Action{implicit request =>
    Product.find(id) match {
      case Some(p) => Ok(Json.toJson(p.image))
      case _ => NotFound(jsonProductNotFound)
    }
  }

  /**
    * Get product
    * @param id ID Product
    * @return Product
    */
  def product(id: Long) = Action{implicit request =>
    Product.find(id) match {
      case Some(p) => {Ok(Json.toJson(p))}
      case _ => NotFound(jsonProductNotFound)
    }
  }

  /**
    * Delete Product of an Seller Company connected or an admin connected
    * @param id ID product
    * @return HTTP code
    */
  def deleteProduct(id: Long) = Action {implicit request =>
    Product.find(id) match {
      case None => NotFound(jsonProductNotFound)
      case Some(p) => {
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case sc: SellerCompany => {
                SellerCompany.tokenConform(c.value, sc) match {
                  case false => Forbidden(jsonRequiredAdmin)
                  case true => {
                    p.available = false
                    p.update()
                    Ok(jsonProductDeleted)
                  }
                }
              }
              case a : Admin => {
                p.delete()
                Ok(jsonProductDeleted)
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    }
  }

  /**
    * Update Product's Image of an Seller Company connected or an admin connected
    * @param id ID product
    * @return HTTP code
    */
  def updateImage(id: Long) = Action {implicit request =>
    Product.find(id) match {
      case None => NotFound(jsonProductNotFound)
      case Some(p) => {
        val i = Image.findByProduct(p).get
        request.body.asMultipartFormData match {
          case Some(mf) => mf.file("image") match {
            case Some(file) => {
              (imageMime.contains(file.contentType.get) && (file.ref.file.length().toFloat/(1024*1024).toFloat) <= 0.1) match {
                case true => {
                  i.name = file.filename
                  i.content = Files.readAllBytes(file.ref.file.toPath)
                  i.mime = file.contentType.get
                  p.image = i
                }
                case false => BadRequest(jsonErrorForm)
              }
            }
            case _ => BadRequest(jsonErrorForm)
          }
          case _ => BadRequest(jsonErrorForm)
        }
        request.cookies.get("token") match {
          case Some(c) => Token.isValid(c.value) match {
            case true => Token.getUser(c.value).get match {
              case u: SellerCompany =>
                SellerCompany.tokenConform(c.value, u) match {
                  case false => Forbidden(jsonRequiredAdmin)
                  case true => {
                    i.update()
                    p.update()
                    Ok(imageUpdated)
                  }
                }
              case a : Admin => {
                i.update()
                p.update()
                Ok(imageUpdated)
              }
              case _ => Forbidden(jsonRequiredAdmin)
            }
            case _ => Forbidden(jsonTokenExpired)
          }
          case _ => Unauthorized(jsonNoToken)
        }
      }
    }
  }

  /**
    * Update Product of an Seller Company connected or an admin connected
    * @param id ID product
    * @return HTTP code
    */
  def updateProduct(id : Long) = Action {implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            SellerCompany.tokenConform(c.value, sc) match {
              case false => Forbidden(jsonRequiredAdmin)
              case true => {
                parameterCreateProduct.bindFromRequest.fold(
                  formWithErrors => BadRequest(jsonErrorForm),
                  formData => {
                    val (description, name, price, quantity) = formData
                    Product.find(id) match {
                      case None => NotFound(jsonProductNotFound)
                      case Some(p) => {
                        p.name = name
                        p.description = description
                        p.price = price.toFloat
                        p.quantity = quantity
                        Product.update(p)
                        Ok(jsonProductUpdated)
                      }
                    }
                  }
                )
              }
            }
          }
          case a : Admin => {
            parameterCreateProduct.bindFromRequest.fold(
              formWithErrors => BadRequest(jsonErrorForm),
              formData => {
                val (description, name, price, quantity) = formData
                Product.find(id) match {
                  case None => NotFound(jsonProductNotFound)
                  case Some(p) => {
                    p.name = name
                    p.description = description
                    p.price = price.toFloat
                    p.quantity = quantity
                    Product.update(p)
                    Ok(jsonProductUpdated)
                  }
                }
              }
            )
          }
          case _ => Forbidden(jsonPermission)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }

  /**
    * Create Product of an Seller Company connected or an admin connected
    * @return HTTP code
    */
  def createProduct = Action {implicit request =>
    request.cookies.get("token") match {
      case Some(c) => Token.isValid(c.value) match {
        case true => Token.getUser(c.value).get match {
          case sc : SellerCompany => {
            parameterCreateProduct.bindFromRequest.fold(
              formWithErrors => BadRequest(jsonErrorForm),
              formData => {
                val (description, name, price, quantity) = formData
                val p = Product(description, name, price.toFloat, quantity, sc)
                p.save()
                request.body.asMultipartFormData match {
                  case Some(mf) => mf.file("image") match {
                    case Some(file) => {
                      (imageMime.contains(file.contentType.get) && (file.ref.file.length().toFloat/(1024*1024).toFloat) <= 0.1) match {
                        case true => {
                          val i = Image(file.filename, file.contentType.get, file.ref.file, p)
                          i.save()
                          p.image = i
                          p.update()
                          Created(jsonProductCreated)
                        }
                        case false => {
                          p.delete()
                          BadRequest(jsonErrorForm)
                        }
                      }
                    }
                    case _ => BadRequest(jsonErrorForm)
                  }
                  case _ => BadRequest(jsonErrorForm)
                }
              }
            )
          }
          case _ => Forbidden(jsonPermission)
        }
        case _ => Forbidden(jsonTokenExpired)
      }
      case _ => Unauthorized(jsonNoToken)
    }
  }
}

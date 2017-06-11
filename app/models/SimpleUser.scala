package models

import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.SimpleUserDAO
import com.avaje.ebean.Model
import com.google.common.hash.Hashing
import controllers.{UserAdress, UserIdentification, UserName}
import play.api.libs.json.{Json, Writes}

/**
  * Entity simple user
  */
@Entity
case class SimpleUser() extends Model with UserIdentification with UserAdress with UserName
{

  /**
    * ID of a simple user
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Email of a simple user
    */
  @Column(unique=true)
  var email : String =_
  /**
    * Password of a simple user
    */
  var password : String =_
  /**
    * Postal code of a simple user
    */
  var postalCode : String =_
  /**
    * Street of a simple user
    */
  var street : String =_
  /**
    * City of a simple user
    */
  var city : String =_
  /**
    * Street number of a simple user
    */
  var streetNumber : String =_
  /**
    * First name of a simple user
    */
  var firstName : String =_
  /**
    * Last name of a simple user
    */
  var lastName : String =_
  /**
    * True if a simple user use facebook to log
    */
  var logFacebook : Boolean =_

  /**
    * Token to authentificate a simple user
    */
  @OneToOne
  var tokenAuthentification : Token =_
  /**
    * Token to reinitialize a password
    */
  @OneToOne
  var tokenReinitialisationEmail : Token =_
  /**
    * List of basket row
    */
  @OneToMany(cascade = Array(CascadeType.ALL))
  var basketRow : java.util.List[BasketRow] =_
  /**
    * List of order
    */
  @OneToMany(cascade = Array(CascadeType.ALL))
  var orders : java.util.List[BasketRow] =_

}

/**
  * Object simple user
  */
object SimpleUser extends SimpleUserDAO {

  def apply(
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             firstName: String,
             lastName: String): SimpleUser = {
    val u = new SimpleUser()
    u.email = email
    u.logFacebook = false
    u.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    u.postalCode = postalCode
    u.street = street
    u.city = city
    u.streetNumber = streetNumber
    u.firstName = firstName
    u.lastName = lastName
    return u
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             firstName: String,
             lastName: String): SimpleUser = {
    val u = new SimpleUser()
    u.id = id
    u.logFacebook = false
    u.email = email
    u.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    u.postalCode = postalCode
    u.street = street
    u.city = city
    u.streetNumber = streetNumber
    u.firstName = firstName
    u.lastName = lastName
    return u
  }

  /**
    * JSon simple user
    */
  implicit val taskWrites = new Writes[SimpleUser] {
    def writes(su: SimpleUser) = Json.obj(
      "id" -> su.id,
      "email" -> su.email,
      "postalCode" -> su.postalCode,
      "street" -> su.street,
      "streetNumber" -> su.streetNumber,
      "city" -> su.city,
      "firstName" -> su.firstName,
      "lastName" -> su.lastName,
      "logFacebook" -> su.logFacebook
    )
  }

}

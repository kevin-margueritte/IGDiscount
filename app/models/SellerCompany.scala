package models

import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.SellerCompanyDAO
import com.avaje.ebean.Model
import com.google.common.hash.Hashing
import controllers.{UserAdress, UserIdentification}
import play.api.libs.json.{Json, Writes}

/**
  * Entity seller company
  */
@Entity
case class SellerCompany() extends Model with UserIdentification with UserAdress
{

  /**
    * ID of seller company
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Email of seller company
    */
  @Column(unique=true)
  var email : String =_
  /**
    * Password of seller company
    */
  var password : String =_
  /**
    * Postal code of seller company
    */
  var postalCode : String =_
  /**
    * Street of seller company
    */
  var street : String =_
  /**
    * City of seller company
    */
  var city : String =_
  /**
    * Street number of seller company
    */
  var streetNumber : String =_
  /**
    * SIRET of seller company
    */
  var siret : String =_
  /**
    * Company name of seller company
    */
  var companyName : String =_

  /**
    * Token to authentificate a seller
    */
  @OneToOne
  var tokenAuthentification : Token =_
  /**
    * Token to reinitialize a password
    */
  @OneToOne
  var tokenReinitialisationEmail : Token =_

  /**
    * List of product on sale
    */
  @OneToMany(cascade = Array(CascadeType.ALL))
  var products : java.util.List[Product] =_
}

/**
  * Object seller company
  */
object SellerCompany extends SellerCompanyDAO {

  def apply(
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             siret: String,
             companyName: String): SellerCompany = {
    val sc = new SellerCompany()
    sc.email = email
    sc.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    sc.postalCode = postalCode
    sc.street = street
    sc.city = city
    sc.streetNumber = streetNumber
    sc.siret = siret
    sc.companyName = companyName
    return sc
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             postalCode: String,
             street: String,
             city: String,
             streetNumber: String,
             siret: String,
             companyName: String): SellerCompany = {
    val sc = new SellerCompany()
    sc.id = id
    sc.email = email
    sc.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    sc.postalCode = postalCode
    sc.street = street
    sc.city = city
    sc.streetNumber = streetNumber
    sc.siret = siret
    sc.companyName = companyName
    return sc
  }

  /**
    * JSon seller company
    */
  implicit val taskWrites = new Writes[SellerCompany] {
    def writes(sc: SellerCompany) = Json.obj(
      "id" -> sc.id,
      "email" -> sc.email,
      "postalCode" -> sc.postalCode,
      "street" -> sc.street,
      "streetNumber" -> sc.streetNumber,
      "city" -> sc.city,
      "siret" -> sc.siret,
      "companyName" -> sc.companyName
    )
  }

}


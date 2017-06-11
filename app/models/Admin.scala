package models

import java.nio.charset.StandardCharsets
import javax.persistence._

import DAO.AdminDAO
import com.avaje.ebean.Model
import com.google.common.hash.Hashing
import controllers.{UserIdentification, UserName}
import play.api.libs.json.{Json, Writes}

/**
  * Entity Admin
  */
@Entity
case class Admin() extends Model with UserIdentification with UserName
{

  /**
    * Id of admin
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Email of admin
    */
  @Column(unique=true)
  var email : String =_
  /**
    * Password of admin
    */
  var password : String =_
  /**
    * First name of Admin
    */
  var firstName : String =_
  /**
    * Last name of admin
    */
  var lastName : String =_

  /**
    * Token to identificate an admin
    */
  @OneToOne
  var tokenAuthentification : Token =_

  /**
    * Token to reinitialize the email of admin
    */
  @OneToOne
  var tokenReinitialisationEmail : Token =_
}

/**
  * Object Admin
  */
object Admin extends AdminDAO {

  def apply(
             email: String,
             password: String,
             firstName: String,
             lastName: String): Admin = {
    val a = new Admin()
    a.email = email
    a.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    a.firstName = firstName
    a.lastName = lastName
    return a
  }

  def apply(
             id : Long,
             email: String,
             password: String,
             firstName: String,
             lastName: String): Admin = {
    val a = new Admin()
    a.id = id
    a.email = email
    a.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
    a.firstName = firstName
    a.lastName = lastName
    return a
  }

  /**
    * JSon Admin
    */
  implicit val taskWrites = new Writes[Admin] {
    def writes(a: Admin) = Json.obj(
      "id" -> a.id,
      "email" -> a.email,
      "password" -> a.password,
      "firstName" -> a.firstName,
      "lastName" -> a.lastName
    )
  }

}


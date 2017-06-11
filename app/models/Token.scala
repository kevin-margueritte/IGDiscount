package models

import java.sql.Timestamp
import java.util.{Calendar, Date, UUID}
import javax.persistence._

import DAO.TokenDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Entity token
  */
@Entity
class Token extends Model {

  /**
    * ID of token
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Expiration date of token
    */
  var expirationDate : Timestamp = _
  /**
    * Unique token content
    */
  @Column(unique=true)
  var token : String =_

}

/**
  * Object token
  */
object Token extends TokenDAO {

  def apply(): Token = {
    val t = new Token()
    t.token = UUID.randomUUID().toString()
    val tomorrow = Calendar.getInstance()
    tomorrow.add(Calendar.DATE, 1)
    t.expirationDate = new Timestamp(tomorrow.getTime.getTime)
    t
  }

  def isValid(t : Token) : Boolean = {
    t.expirationDate.after(new Timestamp(new Date().getTime))
  }

  def isValid(token : String) : Boolean = {
    this.findByToken(token) match {
      case Some(t) => this.isValid(t)
      case _ => false
    }
  }

  /**
    * JSon token
    */
  implicit val taskWrites = new Writes[Token] {
    def writes(t: Token) = Json.obj(
      "id" -> t.id,
      "expirationDate" -> t.expirationDate,
      "token" -> t.token
    )
  }

}



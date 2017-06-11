package models

import javax.persistence._

import DAO.BasketRowDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Entity BasketRow
  */
@Entity
@UniqueConstraint(columnNames = Array("product_id", "simple_user_id"))
class BasketRow extends Model {

  /**
    * Id of basket row
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Quantity of basket row
    */
  var quantity : Int =_
  /**
    * Product into basket row
    */
  @ManyToOne
  var product : Product =_
  /**
    * Owner of basket row
    */
  @ManyToOne
  var simpleUser : SimpleUser =_

}

object BasketRow extends BasketRowDAO {

  def apply(product : Product, simpleUser : SimpleUser, qty : Int) : BasketRow = {
    val br = new BasketRow()
    br.product = product
    br.simpleUser = simpleUser
    br.quantity = qty
    return br
  }

  /**
    * JSon of a basket row
    */
  implicit val taskWrites = new Writes[BasketRow] {
    def writes(br: BasketRow) = Json.obj(
      "id" -> br.id,
      "quantity" -> br.quantity,
      "product" -> br.product,
      "user" -> br.simpleUser
    )
  }
}

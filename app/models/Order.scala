package models

import java.sql.Timestamp
import java.util.Date
import javax.persistence._

import DAO.OrderDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Entity Order
  */
@Entity
@Table(name = "orderProduct")
class Order extends Model {

  /**
    * ID of order
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Quantity of order
    */
  var quantity : Int =_
  /**
    * Update date of order
    */
  var stateDate : Timestamp = _
  /**
    * Sate of order
    */
  var state : String =_
  /**
    * Price of order
    */
  var priceOrder : Float=_
  /**
    * Product of order
    */
  @ManyToOne
  var product : Product =_
  /**
    * Owner of order
    */
  @ManyToOne
  var simpleUser : SimpleUser =_

}

object Order extends OrderDAO {

  def apply(
           quantity : Int,
           state : String,
           priceOrder : Float,
           product : Product,
           simpleUser : SimpleUser
           ) : Order = {
    val o = new Order()
    o.quantity = quantity
    o.stateDate = new Timestamp(new Date().getTime)
    o.state = state
    o.priceOrder = priceOrder
    o.product = product
    o.simpleUser = simpleUser
    o
  }

  /**
    * JSon order
    */
  implicit val taskWrites = new Writes[Order] {
    def writes(o: Order) = Json.obj(
      "id" -> o.id,
      "quantity" -> o.quantity,
      "stateDate" -> o.stateDate,
      "priceOrder" -> o.priceOrder,
      "state" -> o.state,
      "product" -> o.product,
      "simpleUser" -> o.simpleUser
    )
  }

}

/**
  * Enumeration state can be take an order
  */
object OrderState {
  val PENDING = "Pending"
  val CONFIRMED_BY_SELLER = "Confirmed by seller"
  val CANCELLED_BY_SELLER = "Cancelled by seller"
  val PAID = "Paid"
  val SHIPPED = "Shipped"
}
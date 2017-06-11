package models

import javax.persistence._

import DAO.ProductDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}

/**
  * Entity Product
  */
@Entity
case class Product() extends Model {

  /**
    * ID of product
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Description of product
    */
  @Column(length=1000)
  var description : String =_
  /**
    * Name of product
    */
  var name : String =_
  /**
    * Price of product
    */
  var price : Float =_
  /**
    * Quantity of product
    */
  var quantity : Long =_
  /**
    * True if the product is available
    */
  var available : Boolean=_

  /**
    * Owner of product
    */
  @ManyToOne
  var sellerCompany : SellerCompany =_
  /**
    * Image of product
    */
  @OneToOne(cascade = Array(CascadeType.ALL))
  var image : Image =_
  /**
    * List of basket row which contains the product
    */
  @OneToMany(cascade = Array(CascadeType.ALL))
  var basketRow : java.util.List[BasketRow] =_

  /**
    * List of order which contains the product
    */
  @OneToMany(cascade = Array(CascadeType.ALL))
  var orders : java.util.List[BasketRow] =_

}

object Product extends ProductDAO {

  def apply(
             description: String,
             name: String,
             price: Float,
             quantity: Long,
             seller : SellerCompany): Product = {
    val p = new Product()
    p.description = description
    p.available = true
    p.name = name
    p.price = price
    p.quantity = quantity
    p.sellerCompany = seller
    return p
  }

  def apply(
             id: Long,
             description: String,
             name: String,
             price: Float,
             quantity: Long,
             seller : SellerCompany): Product = {
    val p = new Product()
    p.id = id
    p.description = description
    p.name = name
    p.available = true
    p.price = price
    p.quantity = quantity
    p.sellerCompany = seller
    return p
  }

  /**
    * JSon product
    */
  implicit val taskWrites = new Writes[Product] {
    def writes(p: Product) = Json.obj(
      "id" -> p.id,
      "description" -> p.description,
      "name" -> p.name,
      "price" -> p.price,
      "quantity" -> p.quantity,
      "seller" -> p.sellerCompany,
      "image" -> p.image,
      "available" -> p.available
    )
  }

}

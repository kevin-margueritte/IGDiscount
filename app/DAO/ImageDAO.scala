package DAO

import com.avaje.ebean.{Ebean, Expr}
import models.Image

/**
  * Created by kevin on 06/11/16.
  */
class ImageDAO extends DAO(classOf[Image], classOf[Long]) {

  def findByProduct(product : Product) : Option[Image] = {
    Ebean.find(classOf[Image]).where(Expr.eq("product", product)).findUnique() match {
      case null => None
      case i => Some(i)
    }
  }

}
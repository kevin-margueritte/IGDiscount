package DAO

import com.avaje.ebean.{Ebean, Expr}
import models.{Product, SellerCompany}

import scala.collection.JavaConverters._

/**
  * Created by kevin on 05/11/16.
  */
class ProductDAO extends DAO(classOf[Product], classOf[Long]) {

  def findAll(available : Boolean): List[Product] = {
    Ebean.find(classOf[Product]).where(Expr.eq("available",available)).findList().asScala.toList
  }

  def findBySeller(seller : SellerCompany): List[Product] = {
    Ebean.find(classOf[Product]).where(Expr.eq("sellerCompany", seller)).findList().asScala.toList
  }

  def findBySellerAvailability(seller : SellerCompany, available : Boolean): List[Product] = {
    Ebean.find(classOf[Product]).where(Expr.and(Expr.eq("available", available), Expr.eq("sellerCompany", seller))).findList().asScala.toList
  }
}

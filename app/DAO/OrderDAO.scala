package DAO

import com.avaje.ebean.{Ebean, Expr}
import models.{Order, OrderState, SellerCompany}

import scala.collection.JavaConverters._

/**
  * Created by kevin on 18/11/16.
  */
class OrderDAO extends DAO(classOf[Order], classOf[Long]){

  def findAll(state : String): List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.eq("state",state)).findList().asScala.toList
  }

  def findAll(sellerCompany : SellerCompany) : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.eq("product.sellerCompany",sellerCompany)).findList().asScala.toList
  }

  def findAllCancelledOrPaid(sellerCompany : SellerCompany) : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.and(Expr.eq("product.sellerCompany",sellerCompany), Expr.or(Expr.eq("state", OrderState.CANCELLED_BY_SELLER), Expr.eq("state", OrderState.PAID)) )).findList().asScala.toList
  }

  def findAllPendingOrPaid(sellerCompany : SellerCompany) : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.and(Expr.eq("product.sellerCompany",sellerCompany), Expr.or(Expr.eq("state", OrderState.PENDING), Expr.eq("state", OrderState.PAID)) )).findList().asScala.toList
  }

  def findAllConfirmed() : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.eq("state", OrderState.CONFIRMED_BY_SELLER)).findList().asScala.toList
  }

  def findAll(sellerCompany : SellerCompany, state : String) : List[Order] = {
    Ebean.find(classOf[Order]).where(Expr.and(Expr.eq("product.sellerCompany",sellerCompany), Expr.eq("state", state))).findList().asScala.toList
  }

}

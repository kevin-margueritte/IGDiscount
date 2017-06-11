package DAO

import com.avaje.ebean.{Ebean, Expr}
import controllers.UserIdentification
import models.{Admin, SellerCompany, SimpleUser, Token}

/**
  * Created by kevin on 30/10/16.
  */
class TokenDAO extends DAO(classOf[Token], classOf[Long]) {

  def getUser(tokenAuthentification : Token) : Option[UserIdentification] = {
    Ebean.find(classOf[SimpleUser]).where(Expr.eq("tokenAuthentification", tokenAuthentification)).findUnique() match {
      case null => Ebean.find(classOf[SellerCompany]).where(Expr.eq("tokenAuthentification", tokenAuthentification)).findUnique() match {
        case null => Ebean.find(classOf[Admin]).where(Expr.eq("tokenAuthentification", tokenAuthentification)).findUnique() match {
          case null => None
          case x => Some(x)
        }
        case x => Some(x)
      }
      case x => Some(x)
    }
  }

  def getUser(tokenHash : String) : Option[UserIdentification] = {
    this.findByToken(tokenHash) match {
      case Some(t) => this.getUser(t)
      case _ => None
    }
  }

  def findByToken(tokenHash : String): Option[Token] = {
    Ebean.find(classOf[Token]).where(Expr.eq("token", tokenHash)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

}

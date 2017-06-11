package DAO

import java.nio.charset.StandardCharsets

import com.avaje.ebean.{Ebean, Expr}
import com.google.common.hash.Hashing
import models.{SellerCompany, Token}

/**
  * Created by kevin on 30/10/16.
  */
abstract class SellerCompanyDAO extends DAO(classOf[SellerCompany], classOf[Long]) with UserDAO[SellerCompany] {

  override def findByLogin(email : String, psw : String) : Option[SellerCompany] = {
    Ebean.find(classOf[SellerCompany]).where(Expr.and(Expr.eq("email", email), Expr.eq("password", Hashing.sha256().hashString(psw, StandardCharsets.UTF_8).toString()))).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  def findByEmail(email : String) : Option[SellerCompany] = {
    Ebean.find(classOf[SellerCompany]).where(Expr.eq("email", email)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def updatePassword(sc : SellerCompany) : Boolean = {
    super.find(sc.id) match {
      case Some(x) => {
        x.password = sc.password
        super.update(x)
      }
      case _ => false
    }
  }

  override def updateWithoutPassword(sc: SellerCompany) : Boolean = {
    super.find(sc.id) match {
      case Some(x) => {
        sc.password = x.password
        super.update(sc)
      }
      case _ => false
    }
  }

  override def updateTokenAuthentification(sc : SellerCompany, t : Token) : Boolean = {
    sc.tokenAuthentification = t
    super.update(sc)
  }

  override def finByTokenHash(token: Token) : Option[SellerCompany] = {
    Ebean.find(classOf[SellerCompany]).where(Expr.eq("tokenAuthentification", token)).findUnique() match {
      case null => None
      case x => Some(x)
    }
  }

  override def tokenConform(token: Token, sc : SellerCompany) : Boolean = {
    this.finByTokenHash(token) match {
      case Some(x) => x.id.equals(sc.tokenAuthentification.id)
      case _ => false
    }
  }

  override def tokenConform(tokenHash: String, sc : SellerCompany) : Boolean = {
    Token.findByToken(tokenHash) match {
      case Some(x) => this.finByTokenHash(x) match {
        case Some(y) => y.tokenAuthentification.id.equals(sc.tokenAuthentification.id)
        case _ => false
      }
      case _ => false
    }
  }

}

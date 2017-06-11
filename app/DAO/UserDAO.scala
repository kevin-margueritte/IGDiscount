package DAO

import controllers.UserIdentification
import models.Token

/**
  * Created by kevin on 03/11/16.
  */
trait UserDAO[T <: UserIdentification ] {

  def findByLogin(email : String, psw : String) : Option[T]
  def updateTokenAuthentification(u : T, t : Token) : Boolean
  def finByTokenHash(token: Token) : Option[T]
  def tokenConform(token: Token, u : T) : Boolean
  def tokenConform(tokenHash: String, u : T) : Boolean
  def updateWithoutPassword(u: T) : Boolean
  def updatePassword(u : T) : Boolean

}

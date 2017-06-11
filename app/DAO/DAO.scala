package DAO

import com.avaje.ebean.Ebean
import com.avaje.ebean.Model.Finder

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
  * Created by kevin on 24/10/16.
  */
abstract class DAO[T : ClassTag,I](cls: Class[T], id:Class[I]) {

  def finder: Finder[I, T] = new Finder(cls)

  /**
    * Find by Id.
    */
  def find(id: I): Option[T] = {
    finder.byId(id) match {
      case null => None
      case x => Some(x)
    }
  }

  /**
    * Update
    */
  def update(o: T): Boolean = {
    try {
      Ebean.update(o)
      true
    }
    catch {
      case e : Exception => false
    }
  }

  /**
    * Find with expressions and joins etc.
    */
  def findAll(): List[T] = {
    return finder.all().asScala.toList
  }


  /**
    * Save (insert or update).
    */
  def save(o: T): Boolean = {
    try {
      Ebean.save(o)
      true
    }
    catch {
      case e : Exception => false
    }
  }

  /**
    * Delete.
    */
  def delete(o: T): Boolean = {
    try {
      Ebean.delete(o)
      true
    }
    catch {
      case e : Exception => false
    }
  }

  /**
    * Delete by id.
    */
  def deleteById(id: I): Boolean = {
    try {
      Ebean.delete(this.find(id).get)
      true
    }
    catch {
      case e : Exception => false
    }
  }

}

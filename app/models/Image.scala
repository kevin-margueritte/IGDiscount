package models

import java.io.File
import java.nio.file.Files
import javax.persistence._

import DAO.ImageDAO
import com.avaje.ebean.Model
import play.api.libs.json.{Json, Writes}
/**
  * Entity image
  */
@Entity
case class Image() extends Model {

  /**
    * ID of image
    */
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  var id : Long =_
  /**
    * Name of image
    */
  var name : String =_
  /**
    * Mime type of image
    */
  var mime : String =_
  /**
    * Content of image
    */
  @Lob
  var content : Array[Byte] =_
  /**
    * Product of image
    */
  @OneToOne
  var product : Product =_
}

object Image extends ImageDAO {

  def apply(
             name : String,
             mime : String,
             image : File,
             product : Product): Image = {
    val i = new Image()
    i.name = name
    i.mime = mime
    i.product = product
    i.content = Files.readAllBytes(image.toPath)
    return i
  }

  def apply(
             id : Long,
             name : String,
             mime : String,
             product : Product,
             image : File): Image = {
    val i = new Image()
    i.id = id
    i.mime = mime
    i.name = name
    i.product = product
    i.content = Files.readAllBytes(image.toPath)
    return i
  }

  /**
    * JSon of image
    */
  implicit val taskWrites = new Writes[Image] {
    def writes(i: Image) = Json.obj(
      "id" -> i.id,
      "name" -> i.name,
      "product" -> i.id,
      "mime" -> i.mime,
      "content" -> i.content
    )
  }

}

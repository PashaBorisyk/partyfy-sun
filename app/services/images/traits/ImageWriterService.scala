package services.images.traits

import java.awt.image.BufferedImage

import com.google.inject.ImplementedBy
import models.TokenRepPrivate
import models.persistient.Image
import services.images.ImageWriterServiceImpl

import scala.language.higherKinds

@ImplementedBy(classOf[ImageWriterServiceImpl])
trait ImageWriterService [T[_]] {

   final val IMG_SIZE_HUGE_WIDTH = 1440
   final val IMG_SIZE_LARGE_WIDTH = 1080
   final val IMG_SIZE_MEDIUM_WIDTH = 720
   final val IMG_SIZE_SMALL_WIDTH = 360
   final val IMG_SIZE_MINI_WIDTH = 90

   def write(eventId: Long, token: TokenRepPrivate, formatName: String, imageIO: BufferedImage, host:String)
   : T[Image]

}

package services

import java.awt.image.BufferedImage
import java.nio.file.Paths
import javax.imageio.ImageIO

import models.HipeImage
import util._
import org.imgscalr.Scalr
import play.api.mvc.Request
import util.Const._

object ImageWriterService {
   
   def write(eventId:Long,userId:Long,formatName: String,imageIO: BufferedImage)(implicit request: Request[_]): HipeImage = {
      logger.info(s"Image write() format name : $formatName ; ratio : ; width : ${imageIO.getWidth}")
   
      val ratio = imageIO.getHeight.toFloat / imageIO.getWidth.toFloat
      
      lazy val miniName = generateName(formatName)
      lazy val smallName = generateName(formatName)
      lazy val mediumName = generateName(formatName)
      lazy val largeName = generateName(formatName)
      lazy val hugeName = generateName(formatName)
   
      lazy val resultPathMini = s"$PICTURE_PATH/$miniName"
      lazy val resultPathSmall = s"$PICTURE_PATH/$smallName"
      lazy val resultPathMedium = s"$PICTURE_PATH/$mediumName"
      lazy val resultPathLarge = s"$PICTURE_PATH/$largeName"
      lazy val resultPathHuge = s"$PICTURE_PATH/$hugeName"
   
      Map(
         resultPathMini->IMG_SIZE_MINI_WIDTH,
         resultPathSmall->IMG_SIZE_SMALL_WIDTH,
         resultPathMedium->IMG_SIZE_MEDIUM_WIDTH,
         resultPathLarge->IMG_SIZE_LARGE_WIDTH,
         resultPathHuge->IMG_SIZE_HUGE_WIDTH
      ).foreach {
         e =>
            if (imageIO.getWidth > e._2) {
               logger.info("Resizing image")
               ImageIO.write(
                  Scalr.resize(imageIO, Scalr.Method.ULTRA_QUALITY, e._2, (e._2 / ratio).toInt),
                  formatName, Paths.get(e._1).toFile
               )
            }
            else {
               logger.info("Not resizing image")
               ImageIO.write(imageIO, formatName, Paths.get(e._1).toFile)
            }
         
      }
   
      HipeImage(
         ratio = 1f/ratio,
         exist = true,
         eventId = eventId,
         width = imageIO.getWidth(),
         height = imageIO.getHeight(),
         urlMini = getPublicPath(miniName),
         urlSmall = getPublicPath(smallName),
         urlMedium = getPublicPath(mediumName),
         urlLarge = getPublicPath(largeName),
         urlHuge = getPublicPath(hugeName),
         behaviorId = userId
      )
   
   }
   
}

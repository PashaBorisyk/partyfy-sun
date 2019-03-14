package services.images

import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths

import javax.imageio.ImageIO
import javax.inject.Inject
import models.TokenRepPrivate
import models.dto.ImageNames
import models.persistient.Image
import org.imgscalr.Scalr
import play.api.Logger
import services.images.traits.ImageWriterService
import util.generateName

import scala.concurrent.{ExecutionContext, Future}

class ImageWriterServiceImpl @Inject()()(implicit executionContext: ExecutionContext) extends ImageWriterService[Future] {

   private val logger = Logger(this.getClass)

   lazy val PICTURE_PATH = {

      val folder = new File("./public/images")
      val path: String = folder.getPath

      if (!folder.exists())
         folder.mkdirs()
      path
   }

   override def write(eventId: Long, token: TokenRepPrivate, formatName: String, imageIO: BufferedImage, host:String) =
      Future {
      logger.debug(s"Image write() format name : $formatName ; ratio : ; width : ${imageIO.getWidth}")

      val ratio = imageIO.getHeight.toFloat / imageIO.getWidth.toFloat

      val imageNames = createImageNames(formatName)
      val pathSizeMap = getPathSizeMap(imageNames)

      pathSizeMap.foreach {
         pathSize =>
            if (imageIO.getWidth > pathSize._2) {
               logger.debug("Resizing image")
               ImageIO.write(
                  Scalr.resize(imageIO, Scalr.Method.ULTRA_QUALITY, pathSize._2, (pathSize._2 / ratio).toInt),
                  formatName, Paths.get(pathSize._1).toFile
               )
            }
            else {
               logger.debug("Not resizing image")
               ImageIO.write(imageIO, formatName, Paths.get(pathSize._1).toFile)
            }

      }

      val image = createImage(token.userId,eventId,ratio,imageIO.getWidth(),imageIO.getHeight(),imageNames,host)
      image
   }

   private def getPathSizeMap(imageNames: ImageNames) = {


      val resultPathMini = s"$PICTURE_PATH/${imageNames.miniName}"
      val resultPathSmall = s"$PICTURE_PATH/${imageNames.smallName}"
      val resultPathMedium = s"$PICTURE_PATH/${imageNames.mediumName}"
      val resultPathLarge = s"$PICTURE_PATH/${imageNames.largeName}"
      val resultPathHuge = s"$PICTURE_PATH/${imageNames.hugeName}"

      Map(
         resultPathMini -> IMG_SIZE_MINI_WIDTH,
         resultPathSmall -> IMG_SIZE_SMALL_WIDTH,
         resultPathMedium -> IMG_SIZE_MEDIUM_WIDTH,
         resultPathLarge -> IMG_SIZE_LARGE_WIDTH,
         resultPathHuge -> IMG_SIZE_HUGE_WIDTH
      )
   }

   private def createImageNames(formatName: String) = ImageNames(

      miniName = generateName(formatName),
      smallName = generateName(formatName),
      mediumName = generateName(formatName),
      largeName = generateName(formatName),
      hugeName = generateName(formatName),

   )

   private def createImage(userId:Long,eventId:Long,ratio:Float,width:Long,height:Long,imageNames: ImageNames,
                           host:String)
                           = {
      Image(
         ratio = 1f / ratio,
         exist = true,
         eventId = eventId,
         width = width,
         height = height,
         urlMini = getPublicPath(host,imageNames.miniName),
         urlSmall = getPublicPath(host,imageNames.smallName),
         urlMedium = getPublicPath(host,imageNames.mediumName),
         urlLarge = getPublicPath(host,imageNames.largeName),
         urlHuge = getPublicPath(host,imageNames.hugeName),
         behaviorId = userId
      )

   }

   private def getPublicPath(host:String,name: String) = {
      s"$host/public/images/$name"
   }
}

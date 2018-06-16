package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.HipeImageServiceImpl
import models.{HipeImage, HipeImageDAO}
import play.api.mvc.Request

import scala.concurrent.Future

@ImplementedBy(classOf[HipeImageServiceImpl])
trait HipeImageService {

  def create(eventId:Long,hipeImage:HipeImage)(implicit request: Request[_]):Future[Long]
  def delete(id:Long)(implicit request: Request[_]):Future[Int]
  def findById(id:Long)(implicit request: Request[_]):Future[HipeImageDAO#TableElementType]
  def findByEventId(eventId:Long)(implicit request: Request[_]):Future[Seq[HipeImageDAO#TableElementType]]
  def findByUserId(userId:Long)(implicit request: Request[_]):Future[Seq[HipeImageDAO#TableElementType]]

}

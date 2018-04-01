package db.services.interfaces

import com.google.inject.ImplementedBy
import db.services.HipeImageServiceImpl
import models.{HipeImage, HipeImageDAO}

import scala.concurrent.Future

@ImplementedBy(classOf[HipeImageServiceImpl])
trait HipeImageService {

  def create(eventId:Long,hipeImage:HipeImage):Future[Long]
  def delete(id:Long):Future[Int]
  def findById(id:Long):Future[HipeImageDAO#TableElementType]
  def findByEventId(eventId:Long):Future[Seq[HipeImageDAO#TableElementType]]
  def findByUserId(userId:Long):Future[Seq[HipeImageDAO#TableElementType]]

}

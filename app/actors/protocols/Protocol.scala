package actors

import models.persistient.UsersRelationType
import play.api.libs.json.{JsString, Json, OWrites, Writes}

sealed trait Protocol

sealed trait Record {

   val time: Long
   val username: String
   val userId: Int

   def toJson: String

   private[actors] def TOPIC_NAME: String
}

sealed trait EventActionRecord extends Protocol with Record {
   val eventId: Long
}

object EventActionRecord {
   implicit val eventUpdatedFormat: OWrites[EventUpdatedRecord] =
      Json.writes[EventUpdatedRecord]
   implicit val eventDeletedFormat: OWrites[EventDeletedRecord] =
      Json.writes[EventDeletedRecord]
   implicit val eventCreatedFormat: OWrites[EventCreatedRecord] =
      Json.writes[EventCreatedRecord]
   implicit val userWasRemovedFormat: OWrites[EventUserRemovedRecord] =
      Json.writes[EventUserRemovedRecord]
   implicit val userWasAddedFormat: OWrites[EventUserAddedRecord] =
      Json.writes[EventUserAddedRecord]
}

final case class EventUpdatedRecord(
                                      userId: Int,
                                      username: String,
                                      eventId: Long,
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-updated"

   override def toJson = Json.toJson(this).toString()
}

final case class EventDeletedRecord(
                                      userId: Int,
                                      username: String,
                                      eventId: Long,
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-deleted"

   override def toJson = Json.toJson(this).toString()

}

final case class EventCreatedRecord(
                                      userId: Int,
                                      username: String,
                                      eventId: Long,
                                      usersId: Array[Int],
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-created"

   override def toJson = Json.toJson(this).toString()

}

final case class EventUserRemovedRecord(
                                          userId: Int,
                                          username: String,
                                          eventId: Long,
                                          passiveUserId: Int,
                                          time: Long = System.currentTimeMillis()
                                       ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-user-removed"

   override def toJson = Json.toJson(this).toString()

}

final case class EventUserAddedRecord(
                                        userId: Int,
                                        username: String,
                                        eventId: Long,
                                        passiveUserId: Int,
                                        time: Long = System.currentTimeMillis()
                                     ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-user-added"

   override def toJson = Json.toJson(this).toString()

}

sealed trait ImageActionRecord extends Protocol with Record {
   val imageId: Long
}

object ImageActionRecord {
   implicit val imageWasAddedFormat: OWrites[ImageAddedRecord] =
      Json.writes[ImageAddedRecord]
   implicit val userWasAttachedToImageFormat: OWrites[ImageUserAttachedRecord] =
      Json.writes[ImageUserAttachedRecord]
}

final case class ImageAddedRecord(
                                    userId: Int,
                                    username: String,
                                    imageId: Long,
                                    eventId: Long,
                                    markedUsers: Array[Int],
                                    time: Long = System.currentTimeMillis()
                                 ) extends ImageActionRecord {
   override private[actors] val TOPIC_NAME = "image-added"

   override def toJson = Json.toJson(this).toString()
}

final case class ImageUserAttachedRecord(
                                           userId: Int,
                                           username: String,
                                           imageId: Long,
                                           markedUsers: Array[Int],
                                           time: Long = System.currentTimeMillis()
                                        ) extends ImageActionRecord {

   override private[actors] val TOPIC_NAME = "image-user-attached"

   override def toJson = Json.toJson(this).toString()

}

sealed trait UserActionRecord extends Protocol with Record {
   val receiverUser: Int

   implicit val usersRelationTypeWrites: Writes[UsersRelationType] =
      (o: UsersRelationType) => JsString(o.toString)
   implicit val userRelationCreatedWrites: OWrites[UserRelationCreatedRecord] =
      Json.writes[UserRelationCreatedRecord]

}

final case class UserRelationCreatedRecord(
                                             userId: Int,
                                             username: String,
                                             receiverUser: Int,
                                             relationType: UsersRelationType,
                                             time: Long = System.currentTimeMillis()
                                          ) extends UserActionRecord {
   override private[actors] val TOPIC_NAME = "user-relation-created"

   override def toJson = Json.toJson(this).toString()
}

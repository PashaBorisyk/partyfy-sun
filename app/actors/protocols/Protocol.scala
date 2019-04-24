package actors

import models.persistient.UsersRelationType
import play.api.libs.json.{JsString, Json, OWrites, Writes}

sealed trait Protocol

sealed trait Record {

   val time: Long
   val username: String
   val userID: Int

   def toJson: String

   private[actors] def TOPIC_NAME: String
}

sealed trait EventActionRecord extends Protocol with Record {
   val eventID: Long
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
                                      userID: Int,
                                      username: String,
                                      eventID: Long,
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-updated"

   override def toJson = Json.toJson(this).toString()
}

final case class EventDeletedRecord(
                                      userID: Int,
                                      username: String,
                                      eventID: Long,
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-deleted"

   override def toJson = Json.toJson(this).toString()

}

final case class EventCreatedRecord(
                                      userID: Int,
                                      username: String,
                                      eventID: Long,
                                      time: Long = System.currentTimeMillis()
                                   ) extends EventActionRecord {

   override private[actors] val TOPIC_NAME = "event-created"

   override def toJson = Json.toJson(this).toString()

}

sealed trait EventUserAction extends EventActionRecord {
   val receiverID: Int
}

final case class EventUserRemovedRecord(
                                          userID: Int,
                                          username: String,
                                          eventID: Long,
                                          receiverID: Int,
                                          time: Long = System.currentTimeMillis()
                                       ) extends EventUserAction {

   override private[actors] val TOPIC_NAME = "event-user-removed"

   override def toJson = Json.toJson(this).toString()

}

final case class EventUserAddedRecord(
                                        userID: Int,
                                        username: String,
                                        eventID: Long,
                                        receiverID: Int,
                                        time: Long = System.currentTimeMillis()
                                     ) extends EventUserAction {

   override private[actors] val TOPIC_NAME = "event-user-added"

   override def toJson = Json.toJson(this).toString()

}

sealed trait ImageActionRecord extends Protocol with Record {
   val imageID: Long
}

object ImageActionRecord {
   implicit val imageWasAddedFormat: OWrites[ImageAddedRecord] =
      Json.writes[ImageAddedRecord]
   implicit val userWasAttachedToImageFormat: OWrites[ImageUserAttachedRecord] =
      Json.writes[ImageUserAttachedRecord]
}

final case class ImageAddedRecord(
                                    userID: Int,
                                    username: String,
                                    imageID: Long,
                                    eventID: Long,
                                    receiversIDs: Array[Int],
                                    time: Long = System.currentTimeMillis()
                                 ) extends ImageActionRecord {
   override private[actors] val TOPIC_NAME = "image-added"

   override def toJson = Json.toJson(this).toString()
}

final case class ImageUserAttachedRecord(
                                           userID: Int,
                                           username: String,
                                           imageID: Long,
                                           receiversIDs: Array[Int],
                                           time: Long = System.currentTimeMillis()
                                        ) extends ImageActionRecord {

   override private[actors] val TOPIC_NAME = "image-user-attached"

   override def toJson = Json.toJson(this).toString()

}

sealed trait UserActionRecord extends Protocol with Record {
   val receiverID: Int

   implicit val usersRelationTypeWrites: Writes[UsersRelationType] =
      (o: UsersRelationType) => JsString(o.toString)
   implicit val userRelationCreatedWrites: OWrites[UserRelationCreatedRecord] =
      Json.writes[UserRelationCreatedRecord]

}

final case class UserRelationCreatedRecord(
                                             userID: Int,
                                             username: String,
                                             receiverID: Int,
                                             relationType: UsersRelationType,
                                             time: Long = System.currentTimeMillis()
                                          ) extends UserActionRecord {
   override private[actors] val TOPIC_NAME = "user-relation-created"

   override def toJson = Json.toJson(this).toString()
}

package db.services


import controllers.websocket.FrontendWebSocketConnector
import db.services.interfaces.EventService
import javax.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import util.Const

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class EventServiceImpl @Inject()(
                                   val frontendWebSocketConnector: FrontendWebSocketConnector,
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                            )(implicit ec: ExecutionContext)
   extends HasDatabaseConfigProvider[JdbcProfile] with EventService {
   
   val eventTable = TableQuery[EventDAO]
   val userEventTable = TableQuery[EventUserDAO]
   val imageTable = TableQuery[HipeImageDAO]
   
   override def getEventById(id: Long) = {
      val query = (for {
         (event,image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event,image)).filter(_._1.id === id)
   
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
   
   }
   
   override def delete(id: Long) = {
      val result = db.run(eventTable.filter(_.id === id).delete)
      
      frontendWebSocketConnector.webSocketActor! EventMessage[Long](
         `type` = Const.MSG_TYPE_DELETED,
         category = Const.MSG_CATEGORY_ENTITY,
         instanceOf = Const.MSG_INSTANCE_OF_EVENT,
         body = id
      )
      result
   }
   
   override def create(event: (Event, Set[Long])) = Future{
      val query = eventTable returning eventTable.map(_.id)
      val eventId = Await.result(db.run(query += event._1),5.second)
      event._2.foreach{id =>
         db.run(userEventTable+= EventUser(eventId,id))
      }
      
      
      eventId
   }
   
   override def update(event: Event) = {
      val result = db.run(eventTable.filter(_.id === event.id).update(event))
      result
   }
   
   override def getEventsByOwner(userId: Long) = {
      
      val query = (for {
         (event,image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event,image)).filter(_._1.creatorId === userId).sortBy(_._1.id)
   
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
   
   }
   
   override def getEventsByMemberId(userId: Long) = {
      
      val query = (for {
         (event,image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event,image)).filter { e => e._1.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId) }
   
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
   
   }
   
   override def getEventIdsByMemberId(userId:Long) = {
      db.run(eventTable.filter{e => e.id in userEventTable.filter { s => s.userId === userId }.map(_.eventId)}.map(_.id).result)
   }
   
   override def getEvents(userId: Long, latitude: Double, longtitude: Double, lastReadEventId: Long) = {
      
      val query = (for {
         (event,image) <- eventTable joinLeft imageTable on (_.eventImageId === _.id)
      } yield (event,image)).filter {
         entry =>
            entry._1.isPublic === true && entry._1.latitude > (latitude - 0.3) && entry._1.latitude < (latitude + 0.3)
      }.filter {
         entry =>
            entry._1.longtitude > (longtitude - 0.3) && entry._1.longtitude < (longtitude + 0.3)
      }.filter(_._1.id > lastReadEventId).take(30).sortBy(_._1.id.desc)
   
      db.run(query.result).map{
         entry => entry.map{eventImageEntry => (eventImageEntry._1,{
            eventImageEntry._2 match {
               case Some(image) => image
               case None => None
            }
         })}
      }
   }
   
   override def addMemberToEvent(eventId: Long, userId: Long, advancedUserId: Long) = {
      db.run(userEventTable += EventUser(eventId, advancedUserId))
   }
   
   override def cancelEvent(userId: Long, eventId: Long) = {
      
      db.run(eventTable.filter(_.id === eventId).map(_.creatorId).result.head).map {
         id =>
            if (id == userId) {
               db.run(userEventTable.filter{ e => e.eventId === eventId }.delete)
               db.run(eventTable.filter(_.id === eventId).delete)
            }
      }
      
   }
   
   override def removeMember(userId: Long, advancedUserId: Long, eventId: Long) = {
      
      if (userId == advancedUserId)
         db.run(userEventTable.filter { e => e.eventId === eventId && e.userId === userId }.delete)
      else
         db.run(eventTable.filter(_.id === eventId).map(_.creatorId).result.head).map {
            id =>
               if (id == userId) {
                  db.run(userEventTable.filter { e => e.eventId === eventId && e.userId === advancedUserId }.delete)
               }
         }
      
   }
   
}
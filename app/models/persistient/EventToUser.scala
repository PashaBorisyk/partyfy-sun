package models.persistient

case class EventToUser(

                         eventId: Long = 0L,
                         userId: Long = 0L,
                         isNewForMember: Boolean = true

                      )

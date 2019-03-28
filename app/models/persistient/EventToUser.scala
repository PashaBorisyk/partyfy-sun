package models.persistient

case class EventToUser(

                         eventId: Long = 0L,
                         userId: Int = 0,
                         isNewForMember: Boolean = true

                      )

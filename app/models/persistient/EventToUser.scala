package models.persistient

case class EventToUser(
                         eventID: Long = 0L,
                         userID: Int = 0,
                         isNewForMember: Boolean = true
                      )

package models.persistient

final case class Event(
                         id: Long = 0L,
                         ownerId: Int = 0,
                         dateMills: Long = System.currentTimeMillis(),
                         creationDateMills: Long = System.currentTimeMillis(),
                         maxMembers: Long = 0L,
                         longitude: Double = 0.0,
                         latitude: Double = 0.0,
                         ownerUsername: String = "",
                         country: String = "",
                         city: String = "",
                         street: String = "",
                         localName: String = "",
                         description: String = "",
                         openedFor: UserSex = UserSex.ANY,
                         privacy: EventPrivacyType = EventPrivacyType.PUBLIC,
                         eventimageID: Long = 0L,
                         creatorsImageUrl: String = "",
                         state: EventState = EventState.BEFORE
                      )

package models.persistient

final case class Image(

                   id: Long = 0l,
                   width: Long = 0l,
                   ratio: Double = 0d,
                   height: Long = 0l,
                   urlMini: String = "",
                   urlSmall: String = "",
                   urlMedium: String = "",
                   urlLarge: String = "",
                   urlHuge: String = "",
                   ownerId: Long = 0l,
                   eventId: Long = 0l,
                   creationMills: Long = System.currentTimeMillis()

                )


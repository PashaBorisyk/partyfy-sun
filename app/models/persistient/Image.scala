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
                   ownerId: Int = 0,
                   eventId: Long = 0l,
                   creationMills: Long = System.currentTimeMillis()

                )


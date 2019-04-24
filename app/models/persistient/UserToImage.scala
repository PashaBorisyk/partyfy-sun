package models.persistient

final case class UserToImage(
                               userID: Int,
                               imageID: Long,
                               isMarked: Boolean = false,
                               markerID: Int,
                               x: Float = -1,
                               y: Float = -1
                            )

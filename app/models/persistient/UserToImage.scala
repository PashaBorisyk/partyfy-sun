package models.persistient

final case class UserToImage(
                               userId: Int,
                               imageId: Long,
                               isMarked: Boolean = false,
                               markerId: Int,
                               x: Float = -1,
                               y: Float = -1
                            )

package models.persistient


final case class UserToImage(
                         userId: Int,
                         imageId: Long,
                         isMarked:Boolean,
                         x:Float = -1,
                         y:Float = -1
                      )

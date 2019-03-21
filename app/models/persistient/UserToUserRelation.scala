package models.persistient


case class UserToUserRelation(

                        userFrom: Long = 0L,
                        userTo: Long = 0L,
                        relation:UsersRelationType = UsersRelationType.FOLLOW

                     ) extends Serializable


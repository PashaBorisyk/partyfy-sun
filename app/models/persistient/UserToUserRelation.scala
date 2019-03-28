package models.persistient


case class UserToUserRelation(

                        userFrom: Int = 0,
                        userTo: Int = 0,
                        relation:UsersRelationType = UsersRelationType.FOLLOW

                     ) extends Serializable


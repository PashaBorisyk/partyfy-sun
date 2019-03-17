package models.persistient

final case class User(
                  id: Long = 0,
                  username: String = "",
                  token: String = "",
                  name: String = "",
                  surname: String = "",
                  sex:UserSex = UserSex.ANY,
                  isOnline: Boolean = true,
                  status: String = "",
                  latitude: Double = 0.0,
                  longitude: Double = 0.0,
                  imageId: Long = 0L,
                  email:String = "",
                  state:UserState = UserState.REGISTRATION

               )

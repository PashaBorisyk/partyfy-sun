package models.persistient

import com.google.gson.annotations.Expose

import scala.annotation.meta.field

case class User(
                  @(Expose@field)
                  id: Long = -1,
                  @(Expose@field)
                  username: String = "",
                  token: String = "",
                  @(Expose@field)
                  name: String = "",
                  @(Expose@field)
                  surname: String = "",
                  isMale: Boolean = false,
                  isOnline: Boolean = true,
                  status: String = "",
                  latitude: Double = 0.0,
                  longitude: Double = 0.0,
                  @(Expose@field)
                  imageId: Long = 0L,
                  email:String = "",
                  state:UserState = UserState.REGISTRATION

               )

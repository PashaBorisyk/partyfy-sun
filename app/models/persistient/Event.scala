package models.persistient

import com.google.gson.annotations.Expose
import scala.annotation.meta.field

case class Event(
                   id: Long = 0L,
                   creatorId: Long = 0L,
                   dateMills: Long = 0L,
                   creationDateMills: Long = 0L,
                   maxMembers: Long = 0L,
                   @(Expose@field)
                   longitude: Double = 0.0,
                   @(Expose@field)
                   latitude: Double = 0.0,
                   @(Expose@field)
                   creatorNickname: String = "",
                   country: String = "",
                   city: String = "",
                   street: String = "",
                   localName: String = "",
                   @(Expose@field)
                   description: String = "",
                   isPublic: Boolean = false,
                   isForOneGender: Boolean = false,
                   isForMale: Boolean = false,
                   eventImageId: Long = 0L,
                   @(Expose@field)
                   creatorsImageUrl: String = "",

                )
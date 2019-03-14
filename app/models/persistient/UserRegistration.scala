package models.persistient

private object UserRegistrationUtil {
   //2 hours
   final private val REGISTRATION_TIME_ATTEMPT = 720000000L

   def expDate = System.currentTimeMillis() + REGISTRATION_TIME_ATTEMPT
}

case class UserRegistration(
                              id: Long = 0L,
                              username:String = "",
                              emailAddress: String = "",
                              registrationToken: String = "",
                              state: UserRegistrationState = UserRegistrationState.IN_PROGRESS,
                              expirationDateMills: Long = UserRegistrationUtil.expDate

                           )

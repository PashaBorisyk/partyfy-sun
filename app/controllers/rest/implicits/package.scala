package controllers.rest

import models.TokenRepPrivate
import play.api.http.HeaderNames
import play.api.mvc.Request
import services.traits.JWTCoder

package object implicits {

   def getToken(implicit request: Request[_],jwtCoder:JWTCoder): TokenRepPrivate = jwtCoder
      .decodePrivateToken(request
         .headers.get(HeaderNames.AUTHORIZATION)
         .getOrElse(throw new RuntimeException("No AUTHORIZATION header in request")))

}

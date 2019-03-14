package handlers

import javax.inject.Inject
import play.api.Logger
import play.api.http._
import play.api.mvc._
import play.api.routing.Router
import services.traits.JWTCoder

class MyHttpRequestHandler @Inject()(router: Router, jwtCoder: JWTCoder, defaultActionBuilder: DefaultActionBuilder) extends
   HttpRequestHandler {

   private val logger = Logger(this.getClass)

   def handlerForRequest(requestHeader: RequestHeader): (RequestHeader, Handler) = {
      logger.debug(s"Incomming request $requestHeader")
      router.routes.lift(requestHeader) match {
         case Some(handler) =>
            val token = requestHeader.headers.get("Authorization").getOrElse("")
            if (!token.isEmpty) {
               try {
                  val tokenRep = jwtCoder.decodePrivateToken(token)
                  Handler.applyStages(
                     requestHeader.withHeaders(requestHeader.headers), handler
                  )
               } catch {
                  case e: Exception =>
                     logger.debug("Error while parsing token : ", e)
                     (requestHeader, defaultActionBuilder(Results.Unauthorized("Unable to parse token")))
               }

            } else {
               val path = requestHeader.path
               if (path.length > 1l) {
                  path match {
                     case "/user_register/step_one/" | "/user_register/step_two/" | "/user_register/step_three/" =>
                        logger.debug(s"Incoming login request : ${requestHeader.path} with params : ${requestHeader.rawQueryString}")
                        Handler.applyStages(requestHeader, handler)
                     case "event" =>
                        logger.debug(s"Incomming test request : ${requestHeader.path}")
                        Handler.applyStages(requestHeader, handler)
                     case "/user/login/" =>
                        logger.debug(s"Incoming login request : ${requestHeader.path} with params : ${requestHeader.rawQueryString}")
                        Handler.applyStages(requestHeader, handler)
                     case _ => (requestHeader, defaultActionBuilder(Results.Forbidden))
                  }
               }
               else if (path.isEmpty) {
                  Handler.applyStages(requestHeader, handler)
               }
               else {
                  (requestHeader, defaultActionBuilder(Results.Forbidden))
               }
            }


         case None =>
            logger.debug("Returning 404, cause required page not found")
            (requestHeader, defaultActionBuilder(Results.NotFound))

      }
   }
}
package handlers

import implicits.implicits._
import javax.inject.Inject
import play.api.http._
import play.api.mvc._
import play.api.routing.Router
import services.traits.JWTCoder
import util.logger

class MyHttpRequestHandler @Inject()(router: Router, val jwtCoder: JWTCoder) extends HttpRequestHandler {
   def handlerForRequest(requestHeader: RequestHeader): (RequestHeader, Handler) = {
      logger.debug(s"Incomming request $requestHeader")
      router.routes.lift(requestHeader) match {
         case Some(handler) =>
            requestHeader.headers.get("Authorization").getOrElse("").run { token =>
               if (!token.isEmpty) {
                  try {
                     val tokenRep = jwtCoder.decodePrivate(token)
                     Handler.applyStages(
                        requestHeader.withHeaders(requestHeader.headers), handler
                     )
                  } catch {
                     case e: Exception =>
                        logger.debug("Error while parsing token : ", e)
                        (requestHeader, Action(Results.Unauthorized("Unable to parse token")))
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
                        case _ => (requestHeader, Action(Results.Forbidden))
                     }
                  }
                  else if (path.isEmpty) {
                     Handler.applyStages(requestHeader, handler)
                  }
                  else {
                     (requestHeader, Action(Results.Forbidden))
                  }
               }
            }

         case None =>
            logger.debug("Returning 404, cause required page not found")
            (requestHeader, Action(Results.NotFound))

      }
   }
}
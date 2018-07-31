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
                  jwtCoder.decodePrivate(token) match {
                     case (Some(username), Some(_), _) =>
                        Handler.applyStages(requestHeader.withHeaders(
                           requestHeader.headers.add("username" -> username._2)
                        ), handler)
                     case (None, None, None) =>
                        (requestHeader, Action(Results.Unauthorized("Unable to parse token")))
                  }
                  
               } else requestHeader.path match {
                  case "/user/login/" =>
                     logger.debug(s"Incoming login request : ${requestHeader.path}")
                     Handler.applyStages(requestHeader, handler)
                  case "/user/register_step_one/" =>
                     logger.debug(s"Incoming register request : ${requestHeader.path}")
                     Handler.applyStages(requestHeader, handler)
                  case "/user/register_step_two/" =>
                     logger.debug(s"Incoming register request : ${requestHeader.path}")
                     Handler.applyStages(requestHeader, handler)
                  case _ => (requestHeader, Action(Results.Forbidden))
               }
            }
         
         case None =>
            logger.debug("Returning 404, cause required page not found")
            (requestHeader, Action(Results.NotFound))
         
      }
   }
}
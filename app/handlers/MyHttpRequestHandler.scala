package handlers

import javax.inject.Inject
import play.api.Logger
import play.api.http._
import play.api.mvc._
import play.api.routing.Router
import services.traits.JWTCoder

class MyHttpRequestHandler @Inject()(router: Router,
                                     jwtCoder: JWTCoder,
                                     defaultActionBuilder: DefaultActionBuilder)
   extends HttpRequestHandler {

   private val logger = Logger("application")
   var requestCounter:Long = 0

   def handlerForRequest(
                           requestHeader: RequestHeader): (RequestHeader, Handler) = {
      logger.debug(s"Incomming request $requestHeader. Requests handled for this session: ${requestCounter+=1; requestCounter} ")
      router.routes.lift(requestHeader) match {
         case Some(handler) =>
            val token = requestHeader.headers.get("Authorization").getOrElse("")
            if (!token.isEmpty) {
               logger.debug("AUTHORIZATION token found. Trying to parse token")
               try {
                  val tokenRep = jwtCoder.decodePrivateToken(token)
                  Handler.applyStages(
                     requestHeader.withHeaders(requestHeader.headers),
                     handler
                  )
               } catch {
                  case e: Exception =>
                     logger.debug("Error while parsing token : ", e)
                     (requestHeader,
                        defaultActionBuilder(
                           Results.Unauthorized("Unable to parse token")))
               }

            } else {
               logger.debug("Unauthorized user (No token in AUTHORIZATION header)")
               val path = requestHeader.path.split("/")
               if (path.size > 1) {
                  path(1) match {
                     case "user_register" =>
                        logger.debug(
                           s"Incoming login request : ${requestHeader.path} with params : ${requestHeader.rawQueryString}")
                        Handler.applyStages(requestHeader, handler)
                     case "user" if path(2) == "login" =>
                        logger.debug(
                           s"Incoming login request : ${requestHeader.path} with params : ${requestHeader.rawQueryString}")
                        Handler.applyStages(requestHeader, handler)
                     case _ => (requestHeader, defaultActionBuilder(Results.Forbidden))
                  }
               } else if (path.isEmpty) {
                  Handler.applyStages(requestHeader, handler)
               } else {
                  (requestHeader, defaultActionBuilder(Results.Forbidden))
               }
            }

         case None =>
            logger.debug("Returning 404, cause required page not found")
            (requestHeader, defaultActionBuilder(Results.NotFound))

      }
   }
}

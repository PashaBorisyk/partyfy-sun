package filters

import akka.stream.Materializer
import javax.inject.{Inject, _}
import play.api.Logger
import play.api.http.HeaderNames
import play.api.mvc.{Result, _}
import services.traits.JWTCoder

import scala.concurrent.{ExecutionContext, Future}

/**
  * This is a simple filter that adds a header to all requests. It's
  * added to the application's list of filters by the
  * [[Filters]] class.
  *
  * @param mat  This object is needed to handle streaming of requests
  *             and responses.
  * @param exec This class is needed to execute code asynchronously.
  *             It is used below by the `map` method.
  */
@Singleton
class AuthFilter @Inject()(implicit override val mat: Materializer,
                           val jwtCoder: JWTCoder,
                           exec: ExecutionContext) extends Filter {

   private val logger = Logger(this.getClass)

   lazy val unauthorizedMap: Result => Result = {
      _ => Results.Unauthorized
   }

   override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
      // Run the next filter in the chain. This will call other filters
      // and eventually call the action. Take the result and modify it
      // by adding a new header.

      var map: Result => Result = { result => result }
      var initialRequestHeader = requestHeader

      val token = requestHeader.headers.get(HeaderNames.AUTHORIZATION).getOrElse("")
      if (token.nonEmpty) {
         try {

            val tokenRep = jwtCoder.decodePrivateToken(token)
            //todo Rules check

         } catch {
            case e: Exception =>
               logger.debug("Error decoding token : ", e)
               map = unauthorizedMap
         }

         //            match {
         //               case (Some(username), Some(_), _) =>
         //                  initialRequestHeader = requestHeader.withHeaders(
         //                     requestHeader.headers
         //                        .add("username" -> username._2)
         //                  )
         //                  logger.debug(s"${initialRequestHeader.headers}")
         //
         //            }

      } else requestHeader.path match {
         case "/user/login/" => logger.debug(s"Incoming login request : ${requestHeader.path}")
         case "/user/register/" => logger.debug(s"Incoming register request : ${requestHeader.path}")
         case "/event/test/" => logger.debug(s"Incoming test request : ${requestHeader.path}")
         case _ => map = unauthorizedMap
      }

      nextFilter(initialRequestHeader).map(map)

   }
}

package filters

import akka.stream.Materializer
import io.really.jwt.JWTResult.JWT
import javax.inject._
import play.api.Configuration
import util._
import play.api.mvc._
import io.really.jwt.{JWT => JWTDecoder}
import play.api.libs.json.Json
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
class ExampleFilter @Inject()(  implicit override val mat: Materializer,
                                 val jwtCoder:JWTCoder,
                                exec: ExecutionContext) extends Filter {
   
   override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
      // Run the next filter in the chain. This will call other filters
      // and eventually call the action. Take the result and modify it
      // by adding a new header.
      
      val authentication = requestHeader.headers.get("Authorization").getOrElse("")
      if(!authentication.isEmpty) {
         val token = authentication.replaceAll("Basic", "").trim
         logger.debug(s"Token : $token")
         val result = jwtCoder.decode(token)
      }
      nextFilter(requestHeader).map{p=>p}
      
      
      
   }
}

package com.pricehero.http

import com.pricehero.domain.RatesPipe
import com.pricehero.rates.HasRatesService
import com.twitter.finagle.Service
import com.twitter.finagle.http.Status.BadRequest
import com.twitter.finagle.http._
import com.twitter.finagle.http.path.{Path, Root}
import com.twitter.logging.Logger
import com.twitter.util.Future

import scala.collection.JavaConverters._

trait HttpService extends Service[Request, Response] {
  self: HasRatesService =>

  private val logger = Logger.get(this.getClass.getSimpleName)

  def apply(request: Request): Future[Response] = {
      val start: Option[String] = Option(request.getParam("start"))
      val stop: Option[String] = Option(request.getParam("stop"))

      //default to using json
      val format: HttpResponseBodyFormats.Format = HttpResponseBodyFormats.fromString(request.getParam("format"))

      (request.method, Path(request.path), start, stop) match {
        case (Method.Get, Root, Some(startTime), Some(stopTime)) =>
          val ratesPipe: RatesPipe[(String, String), String] = format.getRatesPipe(self.ratesService)

          val attemptPipe = ratesPipe.run((startTime, stopTime)).map { out =>
            val okay = Response()
            okay.setContentString(out)
            logger.info(s"Success: (${start.getOrElse("null")}, ${stop.getOrElse("null")}, $format) -> '$out'")
            okay
          }
          attemptPipe

        case (Method.Get, Root, _, _) =>
          val badRequest = Response(BadRequest)
          val errMsg = (start, stop) match {
            case (Some(_), _) => "Missing 'stop' ISO8601 parameter"
            case (_, Some(_)) => "Missing 'start' ISO8601 parameter"
            case (_, _)       => "Missing both 'start' and 'stop' ISO8601 date-time parameters"
          }
          logger.info(s"\nSending 400 with message: '$errMsg' for request with params: '[${request.getParams.asScala.mkString(",")}]'\n")
          badRequest.setContentString(errMsg)
          Future.value(badRequest)

        case _ => Future.value(Response(Status.NotFound))
      }
    }
}
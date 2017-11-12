package com.pricehero.http

import com.pricehero.rates.HasRatesService
import com.twitter.finagle.{Service, http}
import com.twitter.finagle.http.{Request, Response, _}
import com.twitter.logging.Logger
import com.twitter.util.Future

trait HttpExceptionFiltering extends Service[http.Request, http.Response]{
  self: HasRatesService with HttpService =>

  private val logger = Logger.get(this.getClass.getSimpleName)
  
  abstract override def apply(request: Request): Future[Response] = super.apply(request).handle {
      case e: IllegalArgumentException =>
        val badRequest = Response (Status.BadRequest)
        badRequest.setContentString (Option (e.getMessage).getOrElse ("'start' and/or 'stop' were not ISO8601 date-times") )
        badRequest

      case e: Throwable =>
        val message = Option (e.getMessage).getOrElse ("Something went wrong.")
        logger.error ("\nMessage: %s\nStack trace:\n%s".format (message, e.getStackTrace) )
        Response (Status.InternalServerError)
  }
}

package com.pricehero.app

import com.pricehero.http.{HttpExceptionFiltering, HttpService}
import com.pricehero.rates.{HasRatesService, InMemoryRatesService, JsonRatesService, RatesService}
import com.twitter.finagle.Http
import com.twitter.util.Await

import scala.io.Source

object Main extends App {
  val httpRatesService = new HttpService with HasRatesService with HttpExceptionFiltering {
    val ratesService: RatesService =
      args
        .toSeq
        .find(_.endsWith(".json"))
        // This is a garbage way to do file IO, I'm well aware, but the alternative is me implementing
        // a MapLike interface on a flatfile and that sounds like a great way to waste a Saturday
        .map(Source.fromFile(_).mkString)
        // Call the json rates on the string we read out
        .map(new JsonRatesService(_))
        .getOrElse(new InMemoryRatesService)
  }

  // Couldn't get this to work in a reasonable amount of time, but more or less the only difference is that
  // instead of a RatesPipe[(String, String), String] for the appropriate format, it ought to make a
  // a RatesPipe[ProtoRateQuery, ProtoRateResponse] and run it
  //val protoRatesService = new ProtoRatesService with with HasRatesService { val ratesService: RatesService = ratesService }

  val server = Http.serve(":8080", httpRatesService)

  Await.ready(server)
}
package com.pricehero.app

import com.pricehero.http.{HttpExceptionFiltering, HttpService}
import com.pricehero.rates.{HasRatesService, InMemoryRatesService, JsonRatesService, RatesService}
import com.twitter.finagle.Http
import com.twitter.logging.Logger
import com.twitter.util.Await

import scala.io.Source

object Main extends App {
  val jsonFilePath: String = args.find(_.endsWith(".json")).getOrElse("sample.json")

  Logger.get("Main").info(s"\n!!!! Using args: ${args.toSeq.mkString("[", ", ", "]")} !!!!\n")
  Logger.get("Main").info(s"\n!!!! Using json file: ${jsonFilePath} !!!!\n")

  val src= Source.fromFile(jsonFilePath).mkString

  // This is a garbage way to do file IO, I'm well aware, but the alternative is me implementing
  // a MapLike interface on a flatfile and that sounds like a great way to waste a Saturday
  val httpRatesService = new HttpService with HasRatesService with HttpExceptionFiltering {
    override val ratesService = new JsonRatesService(src)
  }

  val server = Http.serve(":8080", httpRatesService)

  // Couldn't get this to work in a reasonable amount of time, but more or less the only difference is that
  // instead of a RatesPipe[(String, String), String] for the appropriate format, it ought to make a
  // a RatesPipe[ProtoRateQuery, ProtoRateResponse] and run it
  //val protobufRatesService = new ArgsBasedLineHasRatesService(args) with ProtobufRatesService
  //val protoBufServer = ???

  Await.ready(server)
}
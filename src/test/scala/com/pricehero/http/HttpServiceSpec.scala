package com.pricehero.http

import com.pricehero.model.{RateEntry, RatesRegistry}
import com.pricehero.rates.{HasRatesService, InMemoryRatesService}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finagle.http.path.Root
import com.twitter.util.Await
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FlatSpec

class HttpServiceSpec extends FlatSpec {

  it should "404 weird endpoints"  in {
    //given
    val service = new HttpService with HasRatesService {
      val ratesService = new InMemoryRatesService(registry = RatesRegistry(Seq.empty))
    }
    val req = Request("/notAnEndpoint", "start" -> "bleep", "stop" -> "blorp")

    //when
    val futureNotFound = service(req)

    //then
    assert(Await.result(futureNotFound).status == Status.NotFound)
  }

  it should "400 non-iso requests" in {
    //given
    val service = new HttpService with HasRatesService with HttpExceptionFiltering {
      val ratesService = new InMemoryRatesService(registry = RatesRegistry(Seq.empty))
    }
    val req = Request("/", "start" -> "bleep", "stop" -> "blorp")

    //when
    val futureBadRequest = service(req)

    //then
    assert(Await.result(futureBadRequest).status == Status.BadRequest)
  }

  it should "200 valid requests" in {
    //given
    val formatter = ISODateTimeFormat.dateTime
    val start = DateTime.now
    val stop = start.plusHours(1)
    val service = new HttpService with HasRatesService {
      val ratesService = new InMemoryRatesService(registry = RatesRegistry(Seq(
          RateEntry("sun,mon,tues,wed,thurs", "0000-2300", 1000),
          RateEntry("fri,sat", "0000-2300", 1500)
        )))
    }
    val req = Request("/", "start" -> start.toString(), "stop" -> stop.toString())

    //when
    val futureOk = service(req)

    //then
    assert(Await.result(futureOk).status == Status.Ok)
  }
}

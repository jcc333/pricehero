package com.pricehero.http

import com.pricehero.model.RateResponse
import com.twitter.util.Await
import org.scalatest.{Assertions, FlatSpec}

class JsonRatesResponseSerializerSpec extends FlatSpec with Assertions {
  // trait JsonRatesResponseSerializer extends Write[RateResponse, String] {
  val ser = new JsonRatesResponseSerializer{}

  it should "serialize rates responses with present rates" in {
    //given
    val ratesRepsonse = RateResponse(Some(1500))

    //when
    val serialized = ser.serialize(ratesRepsonse)

    //then
    assert(Await.result(serialized) == """{"rate":1500}""")
  }

  it should "serialize rates responses with unavailable rates" in {
    //given
    val ratesRepsonse = RateResponse(None)

    //when
    val serialized = ser.serialize(ratesRepsonse)

    //then
    assert(Await.result(serialized) == """{"rate":"unavailable"}""")
  }
}

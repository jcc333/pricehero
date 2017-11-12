package com.pricehero.http

import com.twitter.util.Await
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FlatSpec

class QueryParamDeserializerSpec extends FlatSpec{
  val de = new QueryParamRateQueryDeserializer {}

  it should "deserialize query ISO8601 param values to datetimes" in {
    //given
    val formatter = ISODateTimeFormat.dateTime
    val start = DateTime.now()
    val stop = start.plusHours(1)
    val isoTimestamppair = (formatter.print(start.getMillis), formatter.print(stop.getMillis))
    //when
    val deserialized = de.deserialize(isoTimestamppair)
    //then
    assert(Await.result(deserialized).start == start)
    assert(Await.result(deserialized).stop == stop)
  }

  it should "throw an exception for query param values which are not ISO8601" in {
    //given
    val notTimestampPair = ("Bleep", "Blorp")
    //when
    val deserialized = de.deserialize(notTimestampPair)
    //then
    assertThrows[IllegalArgumentException](Await.result(deserialized))
  }
}

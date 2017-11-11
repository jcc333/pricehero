package com.pricehero.model

import com.fasterxml.jackson.annotation.JsonValue
import com.twitter.util.Try
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat


object RateQuery {
  val formatter = ISODateTimeFormat.dateTime
  def parse(s: String): Try[DateTime] = Try(formatter.parseDateTime(s))

  def print: DateTime => String = formatter.print

  def tryFromStrings(start: String, end: String): Try[RateQuery] =
    for {
      startDateTime <- parse(start)
      endDateTime <- parse(end)
    } yield {
      new RateQuery(startDateTime, endDateTime)
    }
}

case class RateQuery(@JsonValue start: DateTime, @JsonValue end: DateTime)

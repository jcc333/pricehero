package com.pricehero.model

import com.twitter.util.Try
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

object RateQuery {
  val formatter = ISODateTimeFormat.dateTime

  def parse(s: String): Try[DateTime] = Try(formatter.parseDateTime(s))

  def print: DateTime => String = formatter.print

  def tryFromStrings(start: String, stop: String): Try[RateQuery] =
    parse(start).flatMap { startDt =>
      parse(stop).map { stopDt =>
        new RateQuery(startDt, stopDt)
      }
    }
}

case class RateQuery(start: DateTime, stop: DateTime)

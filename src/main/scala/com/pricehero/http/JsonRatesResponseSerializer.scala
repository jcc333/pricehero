package com.pricehero.http

import com.pricehero.model.RateResponse
import com.pricehero.serde.Ser
import com.twitter.util.Future

// In more complicated scenarios obviously a library like jackson would be useful here, but I'm not on the clock so...
trait JsonRatesResponseSerializer extends Ser[RateResponse, String] {
  def serialize(r: RateResponse): Future[String] =
    r.rate match {
      case Some(n)  => Future.value(s"""{"rate":$n}""")
      case None     => Future.value("""{"rate":"unavailable"}""")
    }
}



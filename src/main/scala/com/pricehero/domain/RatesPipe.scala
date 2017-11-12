package com.pricehero.domain

import com.pricehero.model.{RateQuery, RateResponse}
import com.pricehero.rates.HasRatesService
import com.pricehero.serde.ActionPipe
import com.twitter.util.Future

/**
  * The business logic of rates-lookup
  * @tparam In the param from which we read
  * @tparam Out the param to which we write
  */
trait RatesPipe[In, Out] extends ActionPipe[RateQuery, RateResponse, In, Out] {
  self: HasRatesService =>

  override def translate(query: RateQuery): Future[RateResponse] = {
    ratesService.rateUntil(query.start).map {
      case (rate, stop) =>
        if (query.stop.isBefore(stop)) {
          RateResponse(Some(rate))
        } else {
          RateResponse(None)
        }
    }
  }
}

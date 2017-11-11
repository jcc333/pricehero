package com.pricehero.http

import com.pricehero.domain.RatesPipe
import com.pricehero.rates.{HasRatesService, RatesService}

class JsonRatesPipe(val ratesService: RatesService)
  extends RatesPipe[(String, String), String]
    with QueryParamRateQueryDeserializer
    with JsonRatesResponseSerializer
    with HasRatesService


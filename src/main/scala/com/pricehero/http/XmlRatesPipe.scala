package com.pricehero.http

import com.pricehero.domain.RatesPipe
import com.pricehero.rates.{HasRatesService, RatesService}

class XmlRatesPipe(val ratesService: RatesService)
  extends RatesPipe[(String, String), String]
    with QueryParamRateQueryDeserializer
    with HasRatesService
    with XmlRatesResponseSerializer


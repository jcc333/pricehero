package com.pricehero.rates

/**
  * An object with access to a RatesService; used for DI
  */
trait HasRatesService {
  val ratesService: RatesService
}

package com.pricehero.rates

import com.pricehero.model.RatesRegistry

/**
  * Dummy rates service for use in test
  */
class InMemoryRatesService(val registry: RatesRegistry = RatesRegistry(Seq.empty)) extends RatesService

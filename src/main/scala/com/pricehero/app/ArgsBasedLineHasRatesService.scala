package com.pricehero.app

import com.pricehero.rates.{HasRatesService, InMemoryRatesService, JsonRatesService, RatesService}

import scala.io.Source

trait ArgsBasedLineHasRatesService extends HasRatesService { self: {val args: Array[String]} =>
}

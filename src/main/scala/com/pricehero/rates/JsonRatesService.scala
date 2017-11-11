package com.pricehero.rates

import com.pricehero.model.RatesRegistry
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.reflect.ManifestFactory

/**
  * File-backed rates service. This is by far the least 'real-life software' code in here, but
  * it's a flatfile datastore; there's not that much to do with a finite smal domain size
  * @param jsonBlob the json string from which we get our rates data
  */
class JsonRatesService(jsonBlob: String) extends RatesService {
  import org.json4s.DefaultFormats

  val registry: RatesRegistry = parse(jsonBlob).extract[RatesRegistry](
    DefaultFormats,
    ManifestFactory.classType[RatesRegistry](classOf[RatesRegistry])
  )
}

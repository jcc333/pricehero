package com.pricehero.http

import com.pricehero.domain.RatesPipe
import com.pricehero.rates.RatesService

object HttpResponseBodyFormats {
  sealed trait Format {
    def matchString(s: String): Boolean

    def getRatesPipe(ratesService: RatesService): RatesPipe[(String, String), String]
  }

  object Json extends Format {
    def matchString(s: String): Boolean = s == "JSON" || s == "json"

    def getRatesPipe(ratesService: RatesService): RatesPipe[(String, String), String] = new JsonRatesPipe(ratesService)
  }

  object Xml extends Format {
    def matchString(s: String): Boolean = s == "XML" || s == "xml"

    def getRatesPipe(ratesService: RatesService) = new XmlRatesPipe(ratesService)
  }

  /**
    * @param name the name of the format
    * @return the format object for the name given, or JSON if given a malformed name
    */
  def fromString(name: String): Format =
    if (Xml.matchString(name)) {
      Xml
    } else {
      Json
    }
}




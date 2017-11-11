package com.pricehero.http

import com.pricehero.model.RateQuery
import com.pricehero.serde.De
import com.twitter.util.Future
import com.pricehero.util.CommonConversions._

trait QueryParamRateQueryDeserializer extends De[(String, String), RateQuery] {
  override def deserialize(in: (String, String)): Future[RateQuery] = RateQuery.tryFromStrings(in._1, in._2)
}


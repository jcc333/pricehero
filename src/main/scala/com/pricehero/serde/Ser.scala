package com.pricehero.serde

import com.twitter.util.Future

trait Ser[T, Out] {
  def serialize(t: T): Future[Out]
}

package com.pricehero.serde

import com.twitter.util.Future

trait Write[T, Out] {
  def serialize(t: T): Future[Out]
}

package com.pricehero.serde

import com.twitter.util.Future

trait Read[In, T] {
  def deserialize(in: In): Future[T]
}

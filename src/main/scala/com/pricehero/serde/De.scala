package com.pricehero.serde

import com.twitter.util.Future

trait De[In, T] {
  def deserialize(in: In): Future[T]
}

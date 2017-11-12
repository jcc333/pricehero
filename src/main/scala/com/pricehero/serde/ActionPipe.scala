package com.pricehero.serde

import com.twitter.util.Future

/**
  * Represents an action over domain object A mapped from In to domain object B mapped to Out.
  * @tparam A the input domain object
  * @tparam B the output domain object
  * @tparam In the format from which we deserialize A
  * @tparam Out the format to which we serialize B
  */
trait ActionPipe[A, B, In, Out] extends Read[In, A] with Write[B, Out] {
  def translate(a: A): Future[B]

  def run(in: In): Future[Out] = {
    for {
      a <- deserialize(in)
      b <- translate(a)
      out <- serialize(b)
    } yield {
      out
    }
  }
}

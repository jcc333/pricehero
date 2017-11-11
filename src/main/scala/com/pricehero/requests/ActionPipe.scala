package com.pricehero.requests

import com.pricehero.serde.{De, Ser}
import com.twitter.util.Future

sealed trait ActionResult[Format] {
  val msg: Format
}

/**
  * Represents an action from input to output.
  * @tparam A the input domain object
  * @tparam B the output domain object
  * @tparam In the format from which we deserialize A
  * @tparam Out the format to which we serialize B
  */
trait ActionPipe[A, B, In, Out] extends De[In, A] with Ser[B, Out] {
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

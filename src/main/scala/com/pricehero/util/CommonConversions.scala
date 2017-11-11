package com.pricehero.util

import com.twitter.util.{Future, Return, Throw, Try}

import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom

object CommonConversions {
  implicit def tryProvesFuture[T](t: Try[T]): Future[T] = {
    t match{
      case Return(s) => Future.value(s)
      case Throw(ex) => Future.exception(ex)
    }
  }

  implicit def reprAtoThatB[A, B, Repr <: Traversable[A], That <: Traversable[B]]
  (from: TraversableLike[A, Repr])
  (implicit conv: A => B, bf: CanBuildFrom[Repr, B, That]): That = from map conv
}

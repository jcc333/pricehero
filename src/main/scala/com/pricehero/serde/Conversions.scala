package com.pricehero.serde

import com.twitter.util.Future

import scala.language.experimental._

object SerdeConversionFunctions {
  def fnToDe[In, T](fn: In => Future[T]): Read[In, T] = fn.apply _

  def fnToSer[T, Out](fn: T => Future[Out]): Write[T, Out] = fn.apply _

}

object SerdeConversions {
  implicit def impFnToSer = SerdeConversionFunctions.fnToSer _

  implicit def impFnToDe = SerdeConversionFunctions.fnToDe _
}

object SerdeConverters {
  implicit class FunctionAsDe[In, T](val de: In => Future[T]) {
    def asDeserializer: Read[In, T] = SerdeConversionFunctions.fnToDe(de)
  }

  implicit class FunctionAsSer[T, Out](val ser: T => Future[Out]) {
    def asSerializer: Write[T, Out] = SerdeConversionFunctions.fnToSer(ser)
  }
}

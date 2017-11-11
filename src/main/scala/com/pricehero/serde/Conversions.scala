package com.pricehero.serde

import com.twitter.util.Future

import scala.language.experimental._

object SerdeConversionFunctions {
  def fnToDe[In, T](fn: In => Future[T]): De[In, T] = fn.apply _

  def fnToSer[T, Out](fn: T => Future[Out]): Ser[T, Out] = fn.apply _

}

object SerdeConversions {
  implicit def impFnToSer = SerdeConversionFunctions.fnToSer _

  implicit def impFnToDe = SerdeConversionFunctions.fnToDe _
}

object SerdeConverters {
  implicit class FunctionAsDe[In, T](val de: In => Future[T]) {
    def asDeserializer: De[In, T] = SerdeConversionFunctions.fnToDe(de)
  }

  implicit class FunctionAsSer[T, Out](val ser: T => Future[Out]) {
    def asSerializer: Ser[T, Out] = SerdeConversionFunctions.fnToSer(ser)
  }
}

package com.pricehero.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * scala-idiomatic representation of a response to a rate query
 *
 * @param rate is the pricing information from your specified time-range
 */
case class RateResponse(@JsonValue rate: Option[Int])


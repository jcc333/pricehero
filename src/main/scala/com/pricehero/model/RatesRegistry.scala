package com.pricehero.model

import com.twitter.logging.Logger
import com.twitter.util.{Return, Try}

object RateEntry {
  val logger = Logger.get("RateEntry")
}

case class RateEntry(days: String, times: String, price: Int) {
  private val (start, stop): (Try[Int], Try[Int]) =
    (Try(Integer.parseInt(times.takeWhile(_ != '-')) / 100), Try(Integer.parseInt(times.dropWhile(_ != '-').tail) / 100))

  def hoursSeq: Try[Seq[Int]] = {
    val tryHours = for {
      startHour <- start
      stopHour <- stop
    } yield Range(startHour, stopHour)

    tryHours.onFailure(RateEntry.logger.error(_, s"""Error reading hours from rate entry: {"days":"${days}", "hours":"${times}"""))

    tryHours.rescue(PartialFunction(_ => Return(Seq.empty)))
  }

  def dayNames: Try[Seq[String]] = Return(days.split(','))
}

/**
  * companion object to share common logger instance
  */
object RatesRegistry {
  val logger = Logger.get(RatesRegistry.getClass.getSimpleName)
}

/**
  * in real life, hopefully rates could be a stream or an observable or something like that
  * @param rates the actual chunks of days->hours->rates triples
  */
case class RatesRegistry(rates: Seq[RateEntry]) {
  import com.pricehero.domain.WeekDays._

  val (weekMap, weekSeq): (Map[Int, Map[Int, Int]], Seq[(Int, Int, Int)]) = {
    val rateTriples = rates.flatMap { rateEntry =>
      val tryToGetRatesForEntry = for {
        hours <- rateEntry.hoursSeq
        dayNames <- rateEntry.dayNames
      } yield for {
          day <- dayNames.map(dayNamesToDays)
          hour <- hours
        } yield {
          (day, hour, rateEntry.price)
        }

      tryToGetRatesForEntry.onFailure { case exn: Throwable =>
        val msg = s"Failed to read the rates for days = '${rateEntry.days}', times = '${rateEntry.times}' price = '${rateEntry.price}' -- those times will be unavailable\n"
        RatesRegistry.logger.error(exn, msg)
      }

      tryToGetRatesForEntry.getOrElse(Seq.empty)
    }
    // This shouldn't be necessary, but given duplicate input, they'll discard duplicated entries in a sane fashion
    val weekMap = rateTriples
      // Map by days
      .groupBy(_._1)
      // Map by days then hours
      .mapValues(_.groupBy(_._2))
      // Discard days and hours in innermost map, and ditch duplicate values arbitrarily
      .mapValues(_.mapValues(_.headOption.map(_._3)))
      // Discard any missing values (shouldn't be any, but might as well play it safe
      .mapValues(_.filter(_._2.isDefined))
      // Map from options on rates to rates
      .mapValues(_.mapValues(_.get))

    val weekSeq = weekMap
      .mapValues(_.toSeq)
      .flatMap { case (d, hsToPs) => hsToPs.map { case (h, p) => (d, h, p) } }.toSeq
      .sortBy { case (d, h, _) => d * 24 + h }

    (weekMap, weekSeq)
  }

  /**
    * @return the rates as a sequence of day * hour * rate triples
    */
  def asSeq: Seq[(Int, Int, Int)] = weekSeq

  /**
    * @return the rates as nested map of day to hour to rate
    */
  def asMap: Map[Int, Map[Int, Int]] = weekMap
}



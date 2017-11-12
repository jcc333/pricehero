package com.pricehero.rates

import com.pricehero.domain.WeekDays
import com.pricehero.model.RatesRegistry
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.joda.time.DateTime

/**
  * an abstraction over the only query we ever care to do in the context of this application:
  * "what's the rate for this time, and the range of time for which this query is valid?"
  */
trait RatesService {
  import WeekDays._

  val logger = Logger.get(this.getClass.getSimpleName)

  /**
    * The actual rate-time data object this trait abstracts over, in our case read from a big ole JSON blob
    */
  val registry: RatesRegistry

  /**
    * Return the price at time dt, as well as start and end of the times at which that price is correct
    * @param dt the time we're checking on
    * @return the rate, and the [start, stop) range of the time-range for which that rate is continuously valid
    */
  def rateUntil(dt: DateTime): Future[(Int, DateTime)] = {
    val dayOfTheWeek = dt.dayOfWeek.get
    val hourOfTheDay = dt.hourOfDay.get
    getRate(dayOfTheWeek, hourOfTheDay)
      .map { rate =>
        nextChange(dayOfTheWeek, hourOfTheDay, rate) match {
          case Some((endDay, endHour)) =>
            (rate, dt.plusHours(WeekDays.hoursBetween(dayOfTheWeek, hourOfTheDay, endDay, endHour)) )
          case None =>
            logger.info("Asked for rate for a lot in which the rate is constant for now\n")
            (rate, dt.plusYears(100))
        }
    }
  }

  /**
    * Get the rate for a given hour
    * @param day index of the day (sun -> 0, mon -> 1 etc.)
    * @param hour index of the hour (midnight -> 0, 1AM -> 1 etc.)
    * @return Either an exception explaining why this query was malformed, or the rate
    */
  def getRate(day: Int, hour: Int): Future[Int] = {
    if (hour < 0 || hour > 23) {
      val msg = s"Queried illegal rate data for imaginary hour '$hour'"
      logger.warning(s"\n${msg}\n")
      Future.exception(new IllegalArgumentException(msg))
    }
    else if (day < 0 || day > 6) {
      val msg = s"Queried illegal rate data for imaginary '${if (day == -1)  { "-1st" } else if (day == -2) { "-2nd" } else if (day < 0) { s"-${day}th" } else { s"${day}th" } }' day."
      logger.warning(s"\n${msg}\n")
      Future.exception(new IllegalArgumentException(msg))
    }
    else {
      Future {
        for {
          hours <- registry.asMap.get(day)
          rate <- hours.get(hour)
        } yield rate
      }.flatMap {
        case Some(rate) => Future.value(rate)
        case None =>
          val msg = s"The provided JSON file is missing information for ${daysToDayNames(day)}:$hour"
          logger.warning(s"\n${msg}\n")
          throw new IllegalArgumentException(msg)
      }
    }
  }

  /**
    * If the rates ever change, returns the next time of week when they do. If not, return Future(None)
    *
    * @param day day of our query
    * @param hour hour of our query
    * @param rate the rage (for convenience and filtering)
    * @return the next change this week (or else the first change next week)
    */
  def nextChange(day: Int, hour: Int, rate: Int, firstCall: Boolean = true): Option[(Int, Int)] = {
    registry
      .asSeq
      // Ignore earlier hours of today
      .dropWhile(p => p._1 == day && p._2 <= hour)
      // Ignore the period during which the rate is constant
      .dropWhile(_._3 == rate)
      // Do we have a value?
      .headOption
      .map(t => (t._1, t._2)) match {
      case None if firstCall => nextChange(0, 0, -1, false)
      case None => None
      case something => something
    }
  }
}

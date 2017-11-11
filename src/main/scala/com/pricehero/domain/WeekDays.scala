package com.pricehero.domain

object WeekDays {
  val dayNamesToDays: Map[String, Int] = Seq("sun", "mon", "tues", "wed", "thurs", "fri", "sat").zipWithIndex.toMap
  val daysToDayNames: Map[Int, String] = dayNamesToDays.map(_.swap)

  /**
    * Modular math for the number of hours in the week wrapped up in some weekday/hour-of-day logic
    *
    * @param startDay the index of the day into a 0-indexed week (sun=0, mon=1, etc.)
    * @param startHour the index of the hour into a 0-indexed 24-hour clock (12AM = 0, 1AM = 1, etc.)
    * @param endDay  the index of the day into a 0-indexed week (sun=0, mon=1, etc.)
    * @param endHour the index of the hour into a 0-indexed 24-hour clock (12AM = 0, 1AM = 1, etc.)
    * @return the number of hours between startDay/startHour of this week and endDay/endHour of this week or next week
    */
  def hoursBetween(startDay: Int, startHour: Int, endDay: Int, endHour: Int): Int = {
    val start = startDay * 24 + startHour
    val end = endDay * 24 + endHour
    val hoursInWeek = 7 * 24

    (hoursInWeek - start + end) % hoursInWeek
  }
}

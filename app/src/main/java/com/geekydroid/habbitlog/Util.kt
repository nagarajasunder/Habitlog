package com.geekydroid.habbitlog

import android.content.Context
import com.geekydroid.habbitlog.dao.MonthLogCount
import com.geekydroid.habbitlog.entities.HabitLog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class Util {


    companion object {

        var bestStreak: Int = 0

        const val HABIT_ALARM_NOTIFICATION_CHANNEL_ID = "HABIT_ALARM_CHANNEL"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        fun getNotificationChannelName(context: Context) =
            context.resources.getString(R.string.habit_alarm_notification_name)

        fun getNotificationChannelDescription(context: Context) =
            context.resources.getString(R.string.habit_alarm_notification_description)

        private val formatter12H: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("hh:mm a") }
        private val formatter24H: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm") }

        fun getCurrentTime(is24HourFormat: Boolean): String {
            val time = LocalTime.now()
            return if (is24HourFormat) time.format(formatter24H) else time.format(formatter12H)
        }

        fun getCurrentDate(): Date? {
            val dateStr = LocalDate.now()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return formatter.parse(dateStr.toString())
        }

        fun formatTime(hour: Int, minute: Int, is24HourFormat: Boolean): String {
            val specificTime = LocalTime.of(hour, minute)
            return if (is24HourFormat)
                specificTime.format(formatter24H)
            else
                specificTime.format(formatter12H)
        }


        fun splitHourAndMinute(time: String) = time.replace("\\s".toRegex(), "").split(":")

        fun getFormattedDate(day: Int, month: Int, year: Int): Date? {
            val dateStr =
                (if (day < 10) "0${day}" else "$day") + (if (month < 9) "/0${month + 1}" else "/${month + 1}") + "/$year"
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            return formatter.parse(dateStr)
        }


        fun calculateStreak(habitLog: List<HabitLog>): MutableMap<String, Number> {
            bestStreak = 0

            val dateList: List<String> = habitLog.map { dateFormat.format(it.actionDate!!) }
            val streak = mutableMapOf<String, Number>()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            var ct = 1
            var streakStartDate = ""
            if (dateList.isNotEmpty()) {
                for (i in 0..dateList.size) {
                    if (i < dateList.size - 1) {

                        val currentDateLocale = LocalDate.of(
                            dateList[i].substring(6, 10).toInt(),
                            dateList[i].substring(3, 5).toInt(),
                            dateList[i].substring(0, 2).toInt()
                        ).plusDays(1L)

                        val currentDate = currentDateLocale.format(dateFormatter)
//            val nextDate = LocalDate.parse(dateList[i + 1], dateFormatter).toString()
                        println(currentDate)
                        if (ct == 1) {
                            streakStartDate = dateList[i]
                        }

                        if (dateList.contains(currentDate)) {
                            ct += 1
                        } else {

                            bestStreak = if (ct > bestStreak) ct else bestStreak


                            if (streakStartDate == dateList[i]) {
                                streak.putAll(
                                    formatDates(
                                        date1 = streakStartDate,
                                        date2 = "",
                                        count = ct,
                                        type = 1
                                    )
                                )

                            } else {
                                streak.putAll(formatDates(streakStartDate, dateList[i], ct, 2))

                            }

                            ct = 1
                        }
                    } else {
                        if (ct != 1) {

                            bestStreak = if (ct > bestStreak) ct else bestStreak

                            streak.putAll(
                                formatDates(
                                    streakStartDate,
                                    dateList[dateList.size - 1],
                                    ct,
                                    2
                                )
                            )

                        } else {


                            streak.putAll(formatDates(dateList[dateList.size - 1], "", 1, 1))

                        }
                    }
                }
            }

            return streak
        }

        private fun formatDates(
            date1: String,
            date2: String,
            count: Int,
            type: Int
        ): MutableMap<String, Int> {
            val currentStreak = mutableMapOf<String, Int>()
            val displayFormatter = DateTimeFormatter.ofPattern("dd MMM")
            val displayFormatter2 = DateTimeFormatter.ofPattern("dd")
            val localDate1 = LocalDate.of(
                date1.substring(6, 10).toInt(),
                date1.substring(3, 5).toInt(),
                date1.substring(0, 2).toInt()
            )
            val localDate2 = if (type == 2) {
                LocalDate.of(
                    date2.substring(6, 10).toInt(),
                    date2.substring(3, 5).toInt(),
                    date2.substring(0, 2).toInt()
                )
            } else {
                LocalDate.now()
            }
            if (type == 1) {
                currentStreak[localDate1.format(displayFormatter)] = count
                return currentStreak
            } else {
                if (date1.subSequence(3, 5) == date2.substring(3, 5)) {
                    currentStreak["${localDate1.format(displayFormatter2)} - ${
                        localDate2.format(
                            displayFormatter
                        )
                    }"] = count
                } else {
                    currentStreak["${localDate1.format(displayFormatter)} - ${
                        localDate2.format(
                            displayFormatter
                        )
                    }"] = count
                }
            }

            return currentStreak
        }


        fun fromDateToString(date: Date, format: String): String {
            val sdf = SimpleDateFormat(format, Locale.US)
            return sdf.format(date)
        }

        fun fromLongToTimeString(time:Long,format:String): String? {
            val sdf = SimpleDateFormat(format, Locale.US)
            return sdf.format(time)
        }

        //uses exponential smoothening method
        fun calculateScore(logList: List<MonthLogCount>) {
            if (logList.isNotEmpty()) {
                val alpha = 0.5F
                val scoreList = mutableListOf(logList[0].count.toFloat())
                for (i in 1..logList.size) {
                    val currentScore =
                        alpha * logList[i - 1].count + ((1 - alpha) * scoreList[i - 1])
                    scoreList.add(currentScore)
                }
                println("debug: s $scoreList")
            }
        }

        fun getFormattedCurrentTimeInMs(alarmTime: Long?): String {
            return if (alarmTime == null) DateFormat.getDateTimeInstance()
                .format(System.currentTimeMillis()) else DateFormat.getDateTimeInstance()
                .format(alarmTime)
        }

    }


}
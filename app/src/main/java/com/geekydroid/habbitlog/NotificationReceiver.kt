package com.geekydroid.habbitlog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.geekydroid.habbitlog.entities.HabitLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    private var habitId: Int = 0
    private val currentDate = Util.getCurrentDate()
    private val currentDateFormatted: String = Util.fromDateToString(currentDate!!, "yyyy-MM-dd")

    override fun onReceive(context: Context?, intent: Intent?) {

        habitId = intent?.getIntExtra("HABIT_ID", 0) as Int
        logHabit(context!!, intent)

    }


    private fun logHabit(context: Context,intent: Intent) {

        val db = (context.applicationContext as HabitLogApplication).database
        CoroutineScope(IO).launch {
            val existingLog: HabitLog? = async {
                db.getHabitLogDao()?.getHabitLogByDate(habitId, currentDate!!)
            }.await()
            if (existingLog == null)
            {
                val habitLog = if (intent.action == "Yes") {

                    HabitLog(
                        habitMasterId = habitId,
                        actionType = context.getString(R.string.habit_action_completed),
                        actionDate = currentDate,
                        actionDateStr = currentDateFormatted

                    )

                } else {
                    HabitLog(
                        habitMasterId = habitId,
                        actionType = context.getString(R.string.habit_action_skipped),
                        actionDate = currentDate,
                        actionDateStr = currentDateFormatted
                    )

                }
                db.getHabitLogDao()?.insertHabitLog(habitLog)


            }
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(habitId)

    }
}
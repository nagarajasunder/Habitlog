package com.geekydroid.habbitlog.viewmodels

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.Util
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitLog
import com.geekydroid.habbitlog.receivers.AlarmReceiver
import com.geekydroid.habbitlog.repo.ViewHabitFragmentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ViewHabitFragmentViewModel(private val repo: ViewHabitFragmentRepository, var habitId: Int) :
    ViewModel() {


    var currentDateHabitLog = MutableLiveData<HabitLog?>()
    val actionDates: LiveData<List<HabitLog>> by lazy {
        repo.getHabitLogDates(habitId)
    }

    fun insertHabitLog(habitLog: HabitLog) {
        viewModelScope.launch {
            repo.insertHabitLog(habitLog)
        }
    }

    fun updateHabitLog(habitLog: HabitLog) {
        viewModelScope.launch {
            repo.updateHabitLog(habitLog)
        }
    }

    fun getHabitLogByDate(habitId: Int = 0, actionDate: Date) {
        viewModelScope.launch {
            currentDateHabitLog.value = repo.getHabitLobByDate(habitId, actionDate)
        }
    }

    fun calculateValues(list: List<HabitLog>): String {
        val completedList = list.filter { it.actionType == "Completed" }
        val completionRatio = (completedList.size / list.size.toFloat() * 100)


        return String.format("%.0f", completionRatio)
    }

    fun deleteHabit(habit: Habit, application: HabitLogApplication) {
        viewModelScope.launch {
            cancelAlarm(application, habit)
            repo.deleteHabit(habit)
        }
    }

    private fun cancelAlarm(application: HabitLogApplication, habit: Habit) {
        val intent = Intent(application.applicationContext, AlarmReceiver::class.java)
        intent.action = "HABIT"
        intent.putExtra("HABIT_ID", habitId)

        val alarmIntent =
            PendingIntent.getBroadcast(
                application.applicationContext,
                habitId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val alarmManager = application.alarmManager
        alarmManager.cancel(alarmIntent)
    }

    fun populateData(
        habit: Habit,
        application: HabitLogApplication,
        uri: Uri?,
        bestStreak: String
    ) {
        viewModelScope.launch {
            val data = StringBuilder()
            data.append("Habit Name,Habit Question,Reminder Time,Notes,Best Streak(In Days)\n")
            data.append(
                "${habit.habitName},${habit.habitQuestion},${
                    Util.fromLongToTimeString(
                        habit.alarmTime,
                        "HH:mm"
                    )
                },${habit.habitNotes},${bestStreak}"
            )
            data.append("\nHabit Log History\n")
            val habitLogs: List<HabitLog>? = repo.getAllHabitLogs(habit.habitId)
            if (habitLogs != null) {

                data.append("Action date,Action")
                for (i in habitLogs) {
                    data.append(
                        "\n ${
                            Util.fromDateToString(
                                i.actionDate!!,
                                "dd/MM/yyyy"
                            )
                        },${i.actionType}"
                    )
                }
            }
            createCSV(data, application, uri)

        }
    }


    private fun createCSV(data: StringBuilder, application: HabitLogApplication, uri: Uri?) {
        try {
            val fos = application.contentResolver.openOutputStream(uri!!)
            fos!!.write(data.toString().toByteArray())
            fos.close()
        } catch (e: Exception) {


        }

    }

    fun getLogCountByMonth() {
        viewModelScope.launch {
            val result = async {
                repo.getLogCountByMonth()
            }.await()
            Util.calculateScore(result!!)
        }
    }

//    fun getHabitLogDates(habitId: Int) {
//        println("debug: $habitId")
//        actionDates.postValue(repo.getHabitLogDates(habitId).value)
//        println("debug: ${actionDates.value}")
//    }

}

class ViewHabitFragmentViewModelFactory(
    private val repo: ViewHabitFragmentRepository,
    private val habitId: Int
) :
    ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ViewHabitFragmentRepository::class.java, Int::class.java)
            .newInstance(repo, habitId)
    }


}
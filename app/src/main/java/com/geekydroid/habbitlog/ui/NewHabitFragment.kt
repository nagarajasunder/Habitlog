package com.geekydroid.habbitlog.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import com.geekydroid.habbitlog.Util
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit
import com.geekydroid.habbitlog.viewmodels.CreateNewHabitViewModel
import com.geekydroid.habbitlog.viewmodels.CreateNewHabitViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NewHabitFragment : Fragment(R.layout.fragment_new_habit) {

    private lateinit var fragmentView: View

    private lateinit var edHabitName: TextInputLayout
    private lateinit var edHabitNotes: TextInputLayout
    private lateinit var edHabitQuestion: TextInputLayout
    private lateinit var btnCreateHabit: FloatingActionButton
    private lateinit var tvHabitTime: TextView
    private lateinit var picker: MaterialTimePicker
    private var systemTimeFormat = false
    private var habitTime = ""
    private var habitTimeDB = Util.getCurrentTime(true)
    private var calendar = Calendar.getInstance()
    private val viewmodel: CreateNewHabitViewModel by viewModels {
        CreateNewHabitViewModelFactory(
            (requireActivity().application as HabitLogApplication).createNewhabitRepo,
            requireActivity().application as HabitLogApplication
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view

        setUI()

        tvHabitTime.setOnClickListener {
            showTimePicker()
        }

        btnCreateHabit.setOnClickListener {
            getUserData()
        }
    }

    private fun getUserData() {
        val habitName = edHabitName.editText!!.text.toString()
        val habitQues = edHabitQuestion.editText!!.text.toString()
        val habitNotes = edHabitNotes.editText!!.text.toString()
        if (habitName.isEmpty()) {
            showSnackBar("Please give a name for the habit")
        } else {
            createNewHabit(habitName, habitQues, habitNotes)
        }
    }

    private fun createNewHabit(habitName: String, habitQues: String, habitNotes: String) {
        val habitTimings = Util.splitHourAndMinute(habitTimeDB)
        val habit =
            Habit(
                habitName = habitName,
                habitHour = habitTimings[0].toInt(),
                habitMin = habitTimings[1].toInt(),
                updatedOn = System.currentTimeMillis(),
                alarmTime = calendar.timeInMillis,
                habitQuestion = habitQues,
                habitNotes = habitNotes
            )
        val habitAudit = HabitAudit(habitAuditId = 0, habit = habit)
        viewmodel.createNewHabit(habit, habitAudit)
        showSnackBar("$habitName created successfully!!")


        fragmentView.findNavController().navigateUp()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(fragmentView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setUI() {

        calendar.set(Calendar.SECOND, 0)
        systemTimeFormat = DateFormat.is24HourFormat(requireContext())
        habitTime = Util.getCurrentTime(systemTimeFormat)
        edHabitName = fragmentView.findViewById(R.id.habit_name)
        edHabitQuestion = fragmentView.findViewById(R.id.habit_question)
        edHabitNotes = fragmentView.findViewById(R.id.habit_notes)
        btnCreateHabit = fragmentView.findViewById(R.id.btn_create_habit)
        tvHabitTime = fragmentView.findViewById(R.id.habit_time)
        tvHabitTime.text = habitTime
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTitleText("Select Reminder Time")
            .setTimeFormat(if (systemTimeFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .build()

        picker.show(requireActivity().supportFragmentManager, "tag")


        picker.addOnPositiveButtonClickListener {
            println("debug: ${picker.hour}")
            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            calendar.set(Calendar.MINUTE, picker.minute)
            habitTime =
                Util.formatTime(hour = picker.hour, minute = picker.minute, systemTimeFormat)
            habitTimeDB = Util.formatTime(hour = picker.hour, minute = picker.minute, true)
            tvHabitTime.text = habitTime
        }
    }
}

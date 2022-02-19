package com.geekydroid.habbitlog.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import com.geekydroid.habbitlog.Util
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitAudit
import com.geekydroid.habbitlog.viewmodels.EditHabitFragmentViewModel
import com.geekydroid.habbitlog.viewmodels.EditHabitFragmentViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

private const val TAG = "EditHabitFragment"

class EditHabitFragment : Fragment(R.layout.fragment_edit_habit) {


    private lateinit var fragmentView: View
    private val args: EditHabitFragmentArgs by navArgs()
    private lateinit var currentHabit: Habit
    private lateinit var oldHabit: Habit
    private lateinit var habitName: TextInputLayout
    private lateinit var edHabitNotes: TextInputLayout
    private lateinit var edHabitQues: TextInputLayout
    private lateinit var editHabit: FloatingActionButton
    private lateinit var tvHabitTime: TextView
    private lateinit var habitTime: String
    private lateinit var habitTimeDB: String
    private lateinit var timePicker: MaterialTimePicker
    private var calendar = Calendar.getInstance()
    private var timeFormat = false
    private val viewmodel: EditHabitFragmentViewModel by viewModels {
        EditHabitFragmentViewModelFactory(
            (requireActivity().application as HabitLogApplication).editHabitFragmentRepo,
            (requireActivity().application as HabitLogApplication)
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view


        setUI()

        tvHabitTime.setOnClickListener {
            showTimePicker()
        }

        editHabit.setOnClickListener {
            getUserData()
        }

    }

    private fun getUserData() {
        val habitNameText = habitName.editText?.text.toString()
        val habitTime = Util.splitHourAndMinute(habitTimeDB)
        val habitQues = edHabitQues.editText!!.text.toString()
        val habitNotes = edHabitNotes.editText!!.text.toString()

        if (habitNameText.isEmpty()) {
            showSnackBar("Please give a name for the habit")
        } else {
            currentHabit.habitName = habitNameText
            currentHabit.habitHour = habitTime[0].toInt()
            currentHabit.habitMin = habitTime[1].toInt()
            currentHabit.habitQuestion = habitQues
            currentHabit.habitNotes = habitNotes
            currentHabit.alarmTime = calendar.timeInMillis


            if (compareHabits(currentHabit, oldHabit)) {
                showSnackBar("No changes made in the habit")
            } else {
                currentHabit.updatedOn = System.currentTimeMillis()
                updateHabit(currentHabit, oldHabit)
                showSnackBar("Habit Updated Successfully")
                fragmentView.findNavController().navigateUp()
            }
        }
    }

    private fun compareHabits(currentHabit: Habit, oldHabit: Habit): Boolean {

        return currentHabit == oldHabit
    }

    private fun updateHabit(habit: Habit, oldHabit: Habit) {
        val habitAudit = HabitAudit(habitAuditId = 0, habit = habit)
        viewmodel.updateHabit(habit = habit, habitAudit = habitAudit, oldHabit = oldHabit)

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(fragmentView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setUI() {

        calendar.set(Calendar.SECOND, 0)
        currentHabit = args.habit
        oldHabit = Habit(
            habitId = currentHabit.habitId,
            habitName = currentHabit.habitName,
            habitHour = currentHabit.habitHour,
            habitMin = currentHabit.habitMin,
            createdOn = currentHabit.createdOn,
            updatedOn = currentHabit.updatedOn,
            alarmTime = currentHabit.alarmTime,
            habitQuestion = currentHabit.habitQuestion,
            habitNotes = currentHabit.habitNotes
        )


        timeFormat = DateFormat.is24HourFormat(requireContext())

        habitName = fragmentView.findViewById(R.id.habit_name)
        edHabitNotes = fragmentView.findViewById(R.id.habit_notes)
        edHabitQues = fragmentView.findViewById(R.id.habit_question)
        editHabit = fragmentView.findViewById(R.id.btn_edit_habit)
        tvHabitTime = fragmentView.findViewById(R.id.habit_time)

        //set values to the views
        habitTime = Util.formatTime(
            currentHabit.habitHour.toInt(),
            currentHabit.habitMin.toInt(),
            timeFormat
        )
        habitTimeDB =
            Util.formatTime(currentHabit.habitHour, currentHabit.habitMin, true)
        habitName.editText?.setText(currentHabit.habitName)
        tvHabitTime.text = habitTime
        edHabitNotes.editText?.setText(currentHabit.habitNotes)
        edHabitQues.editText?.setText(currentHabit.habitQuestion)


    }

    private fun showTimePicker() {


        timePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Time")
            .setTimeFormat(if (timeFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .build()

        timePicker.addOnPositiveButtonClickListener {

            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)

            habitTime = Util.formatTime(timePicker.hour, timePicker.minute, timeFormat)
            habitTimeDB = Util.formatTime(timePicker.hour, timePicker.minute, true)
            tvHabitTime.text = habitTime
        }

        timePicker.show(requireActivity().supportFragmentManager, "tag")
    }
}
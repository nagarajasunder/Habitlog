package com.geekydroid.habbitlog.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import com.geekydroid.habbitlog.Util
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.entities.HabitLog
import com.geekydroid.habbitlog.viewmodels.ViewHabitFragmentViewModel
import com.geekydroid.habbitlog.viewmodels.ViewHabitFragmentViewModelFactory
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.card.MaterialCardView
import java.util.*


class ViewHabitFragment : Fragment(R.layout.fragment_view_habit) {
    private lateinit var fragmentView: View
    private lateinit var calendarView: CalendarView
    private lateinit var dayCardView: MaterialCardView
    private lateinit var habitActionText: TextView
    private lateinit var habitTime: TextView
    private lateinit var tvHabitQuestion: TextView
    private lateinit var tvHabitNotes: TextView
    private lateinit var habitAction: ImageView
    private var currentHabitLogDate: Date = Util.getCurrentDate()!!
    private var currentDateHabitLog: HabitLog? = null
    private var currentHabitAction: String = ""
    private lateinit var chart: HorizontalBarChart
    private var dateLabelsLeft = mutableListOf<String>()
    private lateinit var completionValue: TextView
    private lateinit var bestStreak: TextView
    private lateinit var totalValue: TextView
    private var streakMap = mutableMapOf<String, Number>()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data
                viewmodel.populateData(
                    habit,
                    requireActivity().application as HabitLogApplication,
                    uri,
                    Util.bestStreak.toString()
                )
                showToast("Data Exported Successfully!!")
            } else {
                showToast("Error! Exporting Data")
            }
        }
    private val viewmodel: ViewHabitFragmentViewModel by viewModels {
        ViewHabitFragmentViewModelFactory(
            (requireActivity().application as HabitLogApplication).viewHabitFragmentRepo,
            0
        )
    }
    private val args: ViewHabitFragmentArgs by navArgs()
    private lateinit var habit: Habit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        resultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    val uri = it.data!!.data
//                    viewmodel.populateData(
//                        habit,
//                        (requireActivity().application as HabitLogApplication),
//                        uri,
//                        Util.bestStreak.toString()
//                    )
//                    showToast("Data Exported Successfully!!")
//                } else {
//                    showToast("Error! Exporting Data")
//                }
//            }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view



        setHasOptionsMenu(true)
        setUI()

        viewmodel.currentDateHabitLog.observe(viewLifecycleOwner) {
            currentDateHabitLog = it
            currentHabitAction =
                it?.actionType ?: resources.getString(R.string.habit_action_skipped)

            dayCardView.visibility = View.VISIBLE
            if (currentDateHabitLog != null && currentDateHabitLog!!.actionType == resources.getString(
                    R.string.habit_action_completed
                )
            ) {
                completedActionFlow()

            } else {
                skippedActionFlow()
            }
        }

        viewmodel.actionDates.observe(viewLifecycleOwner) { list ->

            val completedList =
                list.filter { it.actionType == getString(R.string.habit_action_completed) }

            if (!completedList.isNullOrEmpty()) {
                setChart(completedList)

            } else {
                chart.clear()
            }
            if (!list.isNullOrEmpty()) {
                val ratios = viewmodel.calculateValues(list)
                updateRatios(ratios, completedList.size)
            }

        }


        dayCardView.setOnClickListener {
            showToast("Press and hold to update")
        }


        dayCardView.setOnLongClickListener {
            if (currentDateHabitLog == null) {
                currentDateHabitLog = HabitLog(
                    habitMasterId = habit.habitId,
                    actionType = resources.getString(R.string.habit_action_completed),
                    actionDate = currentHabitLogDate,
                    actionDateStr = Util.fromDateToString(currentHabitLogDate, "yyyy-MM-dd")
                )

                viewmodel.insertHabitLog(currentDateHabitLog!!)
                completedActionFlow()
            } else {
                if (currentDateHabitLog!!.actionType == resources.getString(R.string.habit_action_completed)) {
                    currentDateHabitLog!!.actionType =
                        resources.getString(R.string.habit_action_skipped)
                    skippedActionFlow()
                } else {
                    currentDateHabitLog!!.actionType =
                        resources.getString(R.string.habit_action_completed)
                    completedActionFlow()
                }

                viewmodel.updateHabitLog(currentDateHabitLog!!)


            }
            showToast("Updated Successfully")
            true
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            currentHabitLogDate = Util.getFormattedDate(day, month, year)!!
            if (Util.isFutureDate(currentHabitLogDate)) {
                dayCardView.visibility = View.INVISIBLE
                showToast("You cannot log habits for future dates")
            } else {
                viewmodel.getHabitLogByDate(habit.habitId, currentHabitLogDate)
            }
        }
    }


    private fun updateRatios(ratios: String, totalCompletion: Int) {
        completionValue.text = getString(R.string.ratio, ratios, "%")
        totalValue.text = totalCompletion.toString()
        bestStreak.text = Util.bestStreak.toString()
    }

    private fun setChart(dateList: List<HabitLog>) {
        streakMap = Util.calculateStreak(dateList)
        val barEntry = ArrayList<BarEntry>()
        var j = 0
        dateLabelsLeft = mutableListOf()
        for (i in streakMap) {
            barEntry.add(BarEntry(j.toFloat(), i.value.toFloat()))
            dateLabelsLeft.add(i.key)
            j++
        }
        val barDataset = BarDataSet(barEntry, "")
        val bardata = BarData(barDataset)
        initBarDataSet(barDataset)
        initBarChart()
        chart.data = bardata
        chart.invalidate()

    }

    private fun initBarDataSet(barDataset: BarDataSet) {
        barDataset.color = resources.getColor(R.color.numbers_color, null)
        barDataset.formSize = 15f
        barDataset.setDrawValues(false)
        barDataset.valueTextSize = 12F
    }

    private fun skippedActionFlow() {
        habitActionText.text = getString(
            R.string.skip_action_text,
            habit.habitName,
            Util.fromDateToString(currentHabitLogDate, "dd/MM/yyyy")
        )

        currentHabitAction = resources.getString(R.string.habit_action_skipped)

        habitAction.apply {
            setImageResource(R.drawable.thumbs_down)
        }
    }

    private fun completedActionFlow() {
        habitActionText.text = getString(
            R.string.complete_action_text,
            habit.habitName,
            Util.fromDateToString(currentHabitLogDate, "dd/MM/yyyy")
        )

        currentHabitAction = resources.getString(R.string.habit_action_completed)

        habitAction.apply {
            setImageResource(R.drawable.thumbs_up)
        }
    }

    private fun setUI() {
        habit = args.habit
        requireActivity().actionBar?.title = habit.habitName
        val systemTimeFormat = DateFormat.is24HourFormat(requireContext())
        viewmodel.habitId = habit.habitId
        calendarView = fragmentView.findViewById(R.id.calendar_view)
        habitTime = fragmentView.findViewById(R.id.tv_habit_time)
        tvHabitNotes = fragmentView.findViewById(R.id.tv_habit_notes)
        tvHabitQuestion = fragmentView.findViewById(R.id.tv_habit_question)
        dayCardView = fragmentView.findViewById(R.id.day_card_view)
        habitActionText = fragmentView.findViewById(R.id.tv_habit_action)
        habitAction = fragmentView.findViewById(R.id.habit_action)
        chart = fragmentView.findViewById(R.id.chartView)
        completionValue = fragmentView.findViewById(R.id.tv_completion_value)
        totalValue = fragmentView.findViewById(R.id.tv_total_value)
        bestStreak = fragmentView.findViewById(R.id.tv_streak_value)

        habitTime.text = Util.formatTime(habit.habitHour, habit.habitMin, systemTimeFormat)
        if (habit.habitQuestion.isNotEmpty()) {
            with(tvHabitQuestion)
            {
                visibility = View.VISIBLE
                text = habit.habitQuestion
            }
        }
        if (habit.habitNotes.isNotEmpty()) {
            with(tvHabitNotes)
            {
                visibility = View.VISIBLE
                text = habit.habitNotes
            }
        }

    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


    private fun initBarChart() {
        //hiding the grey background of the chart, default false if not set
        chart.setDrawGridBackground(false)
        //remove the bar shadow, default false if not set
        chart.setDrawBarShadow(false)
        //remove border of the chart, default false if not set
        chart.setDrawBorders(false)

        //remove the description label text located at the lower right corner
        val description = Description()
        description.isEnabled = false
        chart.description = description

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        chart.animateY(1000)
        //setting animation for x-axis, the bar will pop up separately within the time we set
        chart.animateX(1000)
        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.valueFormatter = MyValueFormatterYAxisLeft()
        //change the position of x-axis to the bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //set the horizontal distance of the grid line
        xAxis.granularity = 1f
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)
        val leftAxis: YAxis = chart.axisLeft
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.valueFormatter = MyValueFormatterXAxisTop()
        val rightAxis: YAxis = chart.axisRight
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        val legend: Legend = chart.legend
        //setting the shape of the legend form to line, default square shape
        legend.form = Legend.LegendForm.LINE
        //setting the text size of the legend
        legend.textSize = 11f
        //setting the alignment of legend toward the chart
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        //setting the stacking direction of legend
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false)
//        chart.axisLeft.valueFormatter = MyValueFormatter()
    }

    inner class MyValueFormatterXAxisTop : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return ""
        }
    }

    inner class MyValueFormatterYAxisLeft : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return try {
                dateLabelsLeft[value.toInt()]
            } catch (e: Exception) {
                ""
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.view_habit_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.edit -> {
                val action = ViewHabitFragmentDirections.actionViewHabitFragmentToEditHabit(habit)
                fragmentView.findNavController().navigate(action)
            }
            R.id.export -> {
                openFilePicker()
            }
            R.id.delete -> {
                showWarningDialog()
            }
        }
        return true
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(
                Intent.EXTRA_TITLE,
                "Habit Log_${Util.getFormattedCurrentTimeInMs(System.currentTimeMillis())}"
            )
        }
        resultLauncher.launch(intent)
    }


    private fun showWarningDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this habit?")
            .setPositiveButton(
                "Yes"
            ) { dialogInterface, _ ->
                deleteHabit()
                dialogInterface?.dismiss()
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog?.dismiss() }
            .create()
        dialog.show()
    }

    private fun deleteHabit() {
        viewmodel.deleteHabit(habit, (requireActivity().application as HabitLogApplication))
        fragmentView.findNavController().navigateUp()
    }


}


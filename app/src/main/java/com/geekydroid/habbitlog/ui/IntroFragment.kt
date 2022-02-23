package com.geekydroid.habbitlog.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R

class IntroFragment(private val currentPage: Int) : Fragment(R.layout.fragment_intro) {
    private lateinit var fragmentView: View
    private lateinit var animText: TextView
    private lateinit var btnStart: Button
    private lateinit var lottieView: LottieAnimationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view
        setUI()

        btnStart.setOnClickListener {

            val prefs =
                requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(
                (requireActivity().application as HabitLogApplication).FIRST_LAUNCH,
                false
            )
            editor.apply()
            val action = ViewpagerHolderDirections.actionViewpagerHolderToHomeFragment()
            fragmentView.findNavController().navigate(action)
        }
    }


    private fun setUI() {
        lottieView = fragmentView.findViewById(R.id.lottie_view)
        btnStart = fragmentView.findViewById(R.id.btn_start)
        animText = fragmentView.findViewById(R.id.anim_text)
        if (currentPage == 0) {
            lottieView.setAnimation(R.raw.hello_lottie)
            btnStart.visibility = View.GONE
            animText.text = getString(R.string.welcome_text)
        } else {
            lottieView.setAnimation(R.raw.stats_lottie)
            btnStart.visibility = View.VISIBLE
            animText.text = getString(R.string.welcome_text_2)
        }

    }
}
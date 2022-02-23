package com.geekydroid.habbitlog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R

class MainActivity : AppCompatActivity() {


    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean((application as HabitLogApplication).FIRST_LAUNCH, true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_graph)
        if (isFirstLaunch) {
            navGraph.setStartDestination(R.id.viewpagerHolder)
        } else {
            navGraph.setStartDestination(R.id.homeFragment)
        }
        navController.graph = navGraph

    }


}
package com.geekydroid.habbitlog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import kotlinx.coroutines.flow.Flow
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {




    val dummy = true
    private lateinit var navController: NavController
    val FIRST_LAUNCH = intPreferencesKey("FIRST_LAUNCH")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navController = navHostFragment.navController
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)
        if (dummy) {
            navGraph.setStartDestination(R.id.viewpagerHolder)
        } else {
            navGraph.setStartDestination(R.id.homeFragment)
        }
        navController.graph = navGraph


    }
}
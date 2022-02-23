package com.geekydroid.habbitlog.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.habbitlog.HabitLogApplication
import com.geekydroid.habbitlog.R
import com.geekydroid.habbitlog.adapters.HabitAdapter
import com.geekydroid.habbitlog.entities.Habit
import com.geekydroid.habbitlog.viewmodels.HomeFragmentViewModel
import com.geekydroid.habbitlog.viewmodels.HomeFragmentViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(R.layout.fragment_home), HabitAdapter.HabitItemOnClickListener {
    private lateinit var fragmentView: View
    private lateinit var createNewHabit: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private var systemTimeFormat: Boolean = false
    private lateinit var searchView: SearchView

    private val viewmodel: HomeFragmentViewModel by viewModels {
        HomeFragmentViewModelFactory((requireActivity().application as HabitLogApplication).homeFragmentRepo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view


        setUI()

        setHasOptionsMenu(true)



        viewmodel.habits.observe(viewLifecycleOwner)
        { list ->
            adapter.submitList(list)
        }


        createNewHabit.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNewHabitFragment()
            fragmentView.findNavController().navigate(action)
        }
    }


    private fun setUI() {

        systemTimeFormat = DateFormat.is24HourFormat(requireContext())

        createNewHabit = fragmentView.findViewById(R.id.btn_add_habit)
        recyclerView = fragmentView.findViewById(R.id.recycler_view)
        adapter = HabitAdapter(this, systemTimeFormat)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter


    }

    override fun onItemClick(habit: Habit) {

        val action = HomeFragmentDirections.actionHomeFragmentToViewHabitFragment(habit)
        fragmentView.findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * The listener can override the standard behavior by returning true
             * to indicate that it has handled the submit request. Otherwise return false to
             * let the SearchView handle the submission by launching any associated intent.
             *
             * @param query the query text that is to be submitted
             *
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            /**
             * Called when the query text is changed by the user.
             *
             * @param newText the new content of the query text field.
             *
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewmodel.searchText.value = newText
                }
                else
                {
                    viewmodel.searchText.value = ""
                }
                return true
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
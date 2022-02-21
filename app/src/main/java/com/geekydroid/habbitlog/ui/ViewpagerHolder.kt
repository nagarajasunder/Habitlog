package com.geekydroid.habbitlog.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.geekydroid.habbitlog.R

const val NUM_PAGES = 2

class ViewpagerHolder : Fragment(R.layout.fragment_viewpager_holder) {
    private lateinit var viewPager: ViewPager2


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = pagerAdapter(this)
    }

    inner class pagerAdapter(fa: ViewpagerHolder) : FragmentStateAdapter(fa) {
        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        override fun getItemCount() = NUM_PAGES

        /**
         * Provide a new Fragment associated with the specified position.
         *
         *
         * The adapter will be responsible for the Fragment lifecycle:
         *
         *  * The Fragment will be used to display an item.
         *  * The Fragment will be destroyed when it gets too far from the viewport, and its state
         * will be saved. When the item is close to the viewport again, a new Fragment will be
         * requested, and a previously saved state will be used to initialize it.
         *
         * @see ViewPager2.setOffscreenPageLimit
         */
        override fun createFragment(position: Int) = IntroFragment(position)


    }
}
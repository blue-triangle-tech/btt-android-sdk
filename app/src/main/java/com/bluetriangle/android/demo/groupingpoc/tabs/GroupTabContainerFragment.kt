package com.bluetriangle.android.demo.groupingpoc.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bluetriangle.android.demo.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class GroupTabContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grouping_tab_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.adapter = TabsAdapter(requireActivity())
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = when(pos) {
                0 -> "First"
                1 -> "Second"
                2 -> "Third"
                3 -> "Fourth"
                else -> "Fifth"
            }
        }.attach()
    }

    class TabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> GroupFirstFragment()
                1 -> GroupSecondFragment()
                2 -> GroupThirdFragment()
                3 -> GroupFourthFragment()
                else -> GroupFifthFragment()
            }
        }

        override fun getItemCount() = 5

    }
}
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


class TabContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Thread.sleep(1000)
        return inflater.inflate(R.layout.fragment_grouping_tab_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        val adapter = TabsAdapter(requireActivity())
        viewPager.adapter = adapter

        // Register page change callback to track current position
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                activity?.setTitle(when(position) {
                    0 -> "Tab One"
                    1 -> null
                    2 -> "Tab Three"
                    3 -> "Tab Four"
                    else -> "Tab Five"
                })
                adapter.setCurrentPosition(position)
            }
        })

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
        private var currentPosition = 0

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> FirstTabFragment()
                1 -> SecondTabFragment()
                2 -> ThirdTabFragment()
                3 -> FourthTabFragment()
                else -> FifthTabFragment()
            }
        }

        override fun getItemCount() = 5

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun containsItem(itemId: Long): Boolean {
            return itemId == currentPosition.toLong()
        }

        fun setCurrentPosition(pos: Int) {
            currentPosition = pos
            notifyDataSetChanged()
        }

    }
}
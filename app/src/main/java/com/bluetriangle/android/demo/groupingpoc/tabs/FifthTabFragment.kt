package com.bluetriangle.android.demo.groupingpoc.tabs

import com.bluetriangle.analytics.Tracker

class FifthTabFragment : TabBaseFragment(0xffFFF5BAL.toInt(), "Fifth Fragment") {
    override fun onResume() {
        super.onResume()
        Tracker.instance?.setScreenName("Fifth Screen")
    }
}
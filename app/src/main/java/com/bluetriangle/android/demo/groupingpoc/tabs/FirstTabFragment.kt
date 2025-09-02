package com.bluetriangle.android.demo.groupingpoc.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.groupingpoc.GroupingConfig
import com.bluetriangle.android.demo.groupingpoc.QuoteRequestHelper
import com.google.android.material.chip.Chip

class FirstTabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_tab_grouping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idleSeekbar = view.findViewById<SeekBar>(R.id.idleSeekbar)
        val idleValue = view.findViewById<TextView>(R.id.seekValue)

        val groupedChip = view.findViewById<Chip>(R.id.grouped_chip)
        val normalChip = view.findViewById<Chip>(R.id.normal_chip)

        GroupingConfig.observeGrouping().asLiveData().observe(viewLifecycleOwner) {
            if(it) {
                groupedChip.isChecked = true
            } else {
                normalChip.isChecked = true
            }
        }
        groupedChip.setOnCheckedChangeListener { v, c ->
            GroupingConfig.setGrouping(c)
        }
        idleSeekbar.max = 4
        idleValue.text = GroupingConfig.getIdleTime().toString()
        idleSeekbar.progress = GroupingConfig.getIdleTime() - 1

        idleSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val idleTime = progress + 1
                idleValue.text = idleTime.toString()
                GroupingConfig.setIdleTime(idleTime)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        QuoteRequestHelper.instance.setupQuoteUI(lifecycleScope, view)
    }

}
package com.bluetriangle.android.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bluetriangle.android.demo.databinding.AnrTestCaseItemLayoutBinding
import com.bluetriangle.android.demo.tests.BTTTestCase

class ANRTestAdapter(
    private val testCases: List<BTTTestCase>,
    private val owner: LifecycleOwner
) : RecyclerView.Adapter<ANRTestAdapter.ANRTestCaseViewHolder>() {

    private var currentTestCase: BTTTestCase? = null

    class ANRTestCaseViewHolder(val itemBinding: AnrTestCaseItemLayoutBinding) :
        ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ANRTestCaseViewHolder(DataBindingUtil.inflate<AnrTestCaseItemLayoutBinding?>(
            LayoutInflater.from(parent.context),
            R.layout.anr_test_case_item_layout,
            parent,
            false
        ).apply {
            lifecycleOwner = owner
        })

    override fun getItemCount() = testCases.size

    override fun onBindViewHolder(holder: ANRTestCaseViewHolder, position: Int) {
        holder.itemBinding.testCase = testCases[position]
        holder.itemBinding.runButton.setOnClickListener {
            currentTestCase = testCases[holder.adapterPosition]
            currentTestCase?.run()
        }
    }

}
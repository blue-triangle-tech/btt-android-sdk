package com.bluetriangle.android.demo.kotlin.screenTracking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bluetriangle.analytics.screenTracking.ScreenTrackMonitor
import com.bluetriangle.android.demo.DemoApplication
import com.bluetriangle.android.demo.databinding.FragmentScreenLogsBinding
import com.bluetriangle.android.demo.databinding.ScreenLogItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenLogFragment : Fragment() {
    companion object {
        fun newInstance() = ScreenLogFragment()
    }

    private var _binding: FragmentScreenLogsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val adapter = Adapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreenLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.listView?.adapter = adapter

        adapter.setEntities((activity?.application as DemoApplication).scrTrack.screenLogs)
    }

    internal class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val entities: ArrayList<ScreenTrackMonitor.ScreenTrackLog> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ScreenLogItemBinding.inflate(LayoutInflater.from(parent.context))
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(entities[position])
        }

        fun setEntities(entities: List<ScreenTrackMonitor.ScreenTrackLog>) {
            this.entities.clear()
            this.entities.addAll(entities)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return entities.size
        }
    }

    internal class ViewHolder(var binding: ScreenLogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ScreenTrackMonitor.ScreenTrackLog) {
            binding.txtScreenName.text = data.screenName
            binding.txtEventName.text = if (data.isLoadEvent) "loaded on" else "viewed on"

            val startTime =
                SimpleDateFormat("yyyy MMM dd, hh:mm:ss a", Locale.US).format(Date(data.startTime))
            val timeTaken = data.timeTaken / 1000f
            binding.txtEventTime.text = "$startTime for $timeTaken sec"
        }
    }
}
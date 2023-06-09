package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetriangle.analytics.screenTracking.ScreenTrackMonitor;
import com.bluetriangle.android.demo.DemoApplication;
import com.bluetriangle.android.demo.databinding.FragmentScreenLogsBinding;
import com.bluetriangle.android.demo.databinding.ScreenLogItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScreenLogFragment extends Fragment {
    public static ScreenLogFragment newInstance() {
        return new ScreenLogFragment();
    }

    private FragmentScreenLogsBinding _binding;

    private final Adapter adapter = new Adapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentScreenLogsBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _binding.listView.setAdapter(adapter);

        adapter.setEntities(((DemoApplication) getActivity().getApplication()).screenTrackMonitor.getScreenLogs());
    }

    static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final ArrayList<ScreenTrackMonitor.ScreenTrackLog> entities = new ArrayList<>();

        public void setEntities(List<ScreenTrackMonitor.ScreenTrackLog> entities) {
            this.entities.clear();
            this.entities.addAll(entities);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ScreenLogItemBinding.inflate(LayoutInflater.from(parent.getContext())));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(entities.get(position));
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ScreenLogItemBinding binding;

        public ViewHolder(ScreenLogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ScreenTrackMonitor.ScreenTrackLog data) {
            binding.txtScreenName.setText(data.getScreenName());
            binding.txtEventName.setText(data.isLoadEvent() ? "loaded on" : "viewed on");

            String startTime = new SimpleDateFormat("yyyy MMM dd, hh:mm:ss a", Locale.US).format(new Date(data.getStartTime()));
            float timeTaken = data.getTimeTaken() / 1000f;
            binding.txtEventTime.setText(String.format("%s for %.3f sec", startTime, timeTaken));
        }
    }
}

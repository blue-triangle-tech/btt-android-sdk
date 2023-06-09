package com.bluetriangle.android.demo.java.screenTracking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bluetriangle.android.demo.R;
import com.google.android.material.tabs.TabLayout;

public class TabViewFragment extends Fragment {
    public static TabViewFragment newInstance() {
        return new TabViewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(requireContext(), getParentFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final Context context;

        SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) return FirstTabFragment.newInstance();
            if (position == 1) return SecondTabFragment.newInstance();
            return new Fragment();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return context.getString(R.string.tab_text_1);
            if (position == 1) return context.getString(R.string.tab_text_2);
            return "";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
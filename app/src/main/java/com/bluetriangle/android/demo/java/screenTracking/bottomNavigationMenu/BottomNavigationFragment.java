package com.bluetriangle.android.demo.java.screenTracking.bottomNavigationMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.FragmentBottomNavigationBinding;

public class BottomNavigationFragment extends Fragment {
    public static BottomNavigationFragment newInstance() {
        return new BottomNavigationFragment();
    }

    private FragmentBottomNavigationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false);
        binding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                loadFragment(HomeFragment.newInstance());
                return true;
            } else if (item.getItemId() == R.id.navigation_dashboard) {
                loadFragment(DashboardFragment.newInstance());
                return true;
            } else if (item.getItemId() == R.id.navigation_notifications) {
                loadFragment(NotificationsFragment.newInstance());
                return true;
            }

            return false;
        });

        loadFragment(new HomeFragment());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}

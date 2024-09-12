package com.bluetriangle.android.demo.kotlin.screenTracking.bottomNavigationMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.FragmentBottomNavigationBinding

class BottomNavigationFragment : Fragment() {
    companion object {
        fun newInstance() = BottomNavigationFragment()
    }

    private var _binding: FragmentBottomNavigationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment.newInstance())
                    true
                }

                R.id.navigation_dashboard -> {
                    loadFragment(DashboardFragment.newInstance())
                    true
                }

                R.id.navigation_notifications -> {
                    loadFragment(NotificationsFragment.newInstance())
                    true
                }

                else -> {
                    loadFragment(NotificationsFragment())
                    true
                }
            }
        }

        loadFragment(HomeFragment())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}
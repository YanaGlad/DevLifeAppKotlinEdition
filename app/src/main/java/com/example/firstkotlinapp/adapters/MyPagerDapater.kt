package com.example.firstkotlinapp.adapters

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.fragments.ButtonSupportedFragment
import com.example.firstkotlinapp.fragments.RandomFragment
import com.example.firstkotlinapp.fragments.RecyclerFragment

class MyPagerAdapter(private val context: Context, manager: FragmentManager?) :
    FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private lateinit var randomFragment: RandomFragment
    private lateinit var recyclerFragment1: RecyclerFragment
    private lateinit var recyclerFragment2: RecyclerFragment
    val currentFragment: ButtonSupportedFragment?
        get() {
            if (randomFragment.isOnScreen) return randomFragment
            if (recyclerFragment1.isOnScreen) return recyclerFragment1
            return if (recyclerFragment2.isOnScreen) recyclerFragment2 else null
        }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.d("PagerAdapterPos", "POS: $position")
        if (randomFragment.isOnScreen && randomFragment !== `object`) {
            randomFragment.isOnScreen = false
        }
        if (recyclerFragment1.isOnScreen && recyclerFragment1 !== `object`) {
            recyclerFragment1.isOnScreen = false
        }
        if (recyclerFragment2.isOnScreen && recyclerFragment2 !== `object`) {
            recyclerFragment2.isOnScreen = false
        }
        when (position) {
            0 -> randomFragment.isOnScreen  = true
            1 -> recyclerFragment1.isOnScreen = true
            2 -> recyclerFragment2.isOnScreen = true
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                randomFragment = RandomFragment()
                return randomFragment
            }
            1 -> {
                recyclerFragment1 = RecyclerFragment.newInstance("latest")
                return recyclerFragment1
            }
            2 -> {
                recyclerFragment2 = RecyclerFragment.newInstance("top")
                return recyclerFragment2
            }
        }
        Log.e("PagerAdapter getItem", "Error at position$position")
        return RandomFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return context.getString(R.string.random)
            1 -> return context.getString(R.string.latest)
            2 -> return context.getString(R.string.top)
        }
        Log.e("PagerAdapter getItem", "Error at position$position")
        return context.getString(R.string.random)
    }

    override fun getCount(): Int {
        return 3
    }
}
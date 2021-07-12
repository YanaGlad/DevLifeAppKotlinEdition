package com.example.firstkotlinapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.firstkotlinapp.adapters.MyPagerAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private var myPagerAdapter: MyPagerAdapter? = null
    private lateinit  var viewPager: ViewPager
    private var btnNext: ExtendedFloatingActionButton? = null
    private var btnPrevious: ExtendedFloatingActionButton? = null
    private lateinit var tabLayout: TabLayout
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            btnNext!!.setOnClickListener(
                myPagerAdapter?.currentFragment?.onNextClickListener
            )
            btnPrevious!!.setOnClickListener(
                myPagerAdapter?.currentFragment?.onPrevClickListener
            )
            btnNext!!.isEnabled = myPagerAdapter?.currentFragment!!.nextEnabled()
            btnPrevious!!.isEnabled = myPagerAdapter?.currentFragment!!.previousEnabled()
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myPagerAdapter = MyPagerAdapter(applicationContext, supportFragmentManager)
        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.view_pager)
        viewPager.setOffscreenPageLimit(3)
        viewPager.setAdapter(myPagerAdapter)
        viewPager.addOnPageChangeListener(onPageChangeListener)
        tabLayout.setupWithViewPager(viewPager)
        btnNext = findViewById(R.id.btn_next)
        btnPrevious = findViewById(R.id.btn_previous)
    }
}


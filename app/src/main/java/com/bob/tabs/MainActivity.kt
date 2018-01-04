package com.bob.tabs

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slideLayout.addTab("DOGS", R.drawable.dog)
        slideLayout.addTab("CATS", R.drawable.cat)
        slideLayout.addTab("RODENTS", R.drawable.mouse)
        slideLayout.addTab("FISHES", R.drawable.fish)
        slideLayout.addTab("BIRDS", R.drawable.parrot)

        slideLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    // you selected tab at position tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
}

package com.zero.tzz.pageindexview.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.zero.tzz.pageindexview.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRV()
    }

    private fun initRV() {
        recyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val list = arrayListOf<String>()
        for (i in 1..300) {
            list.add(i.toString())
        }
        recyclerView.adapter = TestAdapter(list, recyclerView, 8)
        pageIndexView.show(recyclerView, list.size, pageCount = 8)
    }
}

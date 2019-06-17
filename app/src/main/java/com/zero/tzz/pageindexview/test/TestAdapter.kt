package com.zero.tzz.pageindexview.test

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zero.tzz.pageindexview.R

/**
 *
 * @author lucy
 * @date 2019-06-17 15:47
 * @description //TODO
 */
class TestAdapter(private val list: List<String>?, private val recyclerView: RecyclerView, private val pageItemCount: Int = 6) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    override fun onBindViewHolder(holder: TestViewHolder?, position: Int) {
        holder?.tvContent?.text = list?.get(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_page_index, parent, false)
        view.layoutParams.height = recyclerView.height / pageItemCount
        return TestViewHolder(view)
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContent: TextView = itemView.findViewById(R.id.tv_content)
    }
}
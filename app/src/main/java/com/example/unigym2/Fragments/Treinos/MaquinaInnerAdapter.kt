package com.example.unigym2.Fragments.Treinos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R

class MaquinaInnerAdapter(private val innerItems: List<MaquinaInnerItem>) :
    RecyclerView.Adapter<MaquinaInnerAdapter.InnerViewHolder>() {

    class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.inner_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maquinas_inner_recycler_layout, parent, false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.textView.text = innerItems[position].text
    }

    override fun getItemCount(): Int {
        return innerItems.size
    }
}
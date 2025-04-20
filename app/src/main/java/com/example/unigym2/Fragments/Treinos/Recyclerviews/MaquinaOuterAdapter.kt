package com.example.unigym2.Fragments.Treinos.Recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R

class MaquinaOuterAdapter(private val outerItems: List<MaquinaOuterItem>, private val viewPool: RecyclerView.RecycledViewPool) :
    RecyclerView.Adapter<MaquinaOuterAdapter.OuterViewHolder>() {

    class OuterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maquinas_outer_recycler_layout, parent, false)
        return OuterViewHolder(view)
    }

    override fun onBindViewHolder(holder: OuterViewHolder, position: Int) {
        val outerItem = outerItems[position]

        // Setup inner RecyclerView
        holder.innerRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.innerRecyclerView.adapter = MaquinaInnerAdapter(outerItem.innerItems)

        // Para otimizar
        holder.innerRecyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int {
        return outerItems.size
    }
}
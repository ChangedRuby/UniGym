package com.example.unigym2.Fragments.Treinos.Recyclerviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Treinos.AdicionarExercicioAMaquina
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

class MaquinaOuterAdapter(private val outerItems: MutableList<MaquinaOuterItem>, private val viewPool: RecyclerView.RecycledViewPool, private val communicator: Communicator, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MaquinaOuterAdapter.OuterViewHolder>() {

    class OuterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recycler_view)
        val addExercicioAMaquinaBtn: View = itemView.findViewById(R.id.addExerciciosAMaquinaButton)
        val deleteMaquinaButton: View = itemView.findViewById(R.id.deleteMaquinaBtn)
        val maquinaTitle: TextView = itemView.findViewById(R.id.maquinaName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maquinas_outer_recycler_layout, parent, false)
        return OuterViewHolder(view)
    }

    override fun onBindViewHolder(holder: OuterViewHolder, position: Int) {
        var outerItem = outerItems[position]
        val db = FirebaseFirestore.getInstance()

        // Setup inner RecyclerView
        holder.innerRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        holder.innerRecyclerView.adapter = MaquinaInnerAdapter(outerItem.innerItems ?: mutableListOf()) // retorna mutableList vazia se null
        holder.maquinaTitle.text = outerItem.title

        holder.addExercicioAMaquinaBtn.setOnClickListener{
            fragmentManager.setFragmentResult("maquina_info_key", Bundle().apply {
                putString("maquina_name", holder.maquinaTitle.text.toString())
                putString("maquina_id", outerItem.id)
            })

            communicator.replaceFragment(AdicionarExercicioAMaquina())
        }

        holder.deleteMaquinaButton.setOnClickListener {
            val id = outerItem.id.toString()

            outerItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, outerItems.size)

            db.collection("Maquinas").document(id).delete().addOnCompleteListener {

                Log.d("deleteMaquinaButton", "DocumentSnapshot $id successfully deleted")
            }

        }


        // Para otimizar
        // holder.innerRecyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int {
        return outerItems.size
    }
}
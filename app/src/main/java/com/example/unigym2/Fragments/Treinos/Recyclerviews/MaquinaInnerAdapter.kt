package com.example.unigym2.Fragments.Treinos.Recyclerviews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore

class MaquinaInnerAdapter(private val innerItems: MutableList<MaquinaInnerItem>) :
    RecyclerView.Adapter<MaquinaInnerAdapter.InnerViewHolder>() {

    class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.inner_item_text)
        val deleteExercicioButton: Button = itemView.findViewById(R.id.removeExercicioBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maquinas_inner_recycler_layout, parent, false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val innerItem = innerItems[position]
        holder.textView.text = innerItem.text

        val db = FirebaseFirestore.getInstance()
        val document = db.collection("Maquinas").document(innerItem.maquinaId.toString())

        var exercisesArray: ArrayList<*>

        holder.deleteExercicioButton.setOnClickListener {
            innerItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, innerItems.size)

            document.get().addOnSuccessListener { result ->
                exercisesArray = result.data?.get("exercises") as ArrayList<*>
                exercisesArray.removeAt(position)

                document.update(
                    hashMapOf<String, Any>(
                        "exercises" to exercisesArray
                    ),
                ).addOnSuccessListener { result ->
                    Log.d("maquinas_data", "Exercise in position $position successfully removed.")
                }

            }

        }
    }

    override fun getItemCount(): Int {
        return innerItems.size
    }
}
package com.example.unigym2.Fragments.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsData
import com.example.unigym2.Fragments.Home.Recyclerviews.RequestsRecyclerAdapter
import com.example.unigym2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [SolicitationsPersonal.newInstance] factory method to
 * create an instance of this fragment.
 */
class SolicitationsPersonal : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var schedulesBtn: Button
    private lateinit var communicator: Communicator

    private lateinit var adapter : RequestsRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestsArrayList: ArrayList<RequestsData>

    lateinit var names: Array<String>
    lateinit var requests: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_solicitations_personal, container, false)

        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RequestsRecyclerAdapter(requestsArrayList)
        recyclerView.adapter = adapter

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SolicitationsPersonal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                SolicitationsPersonal().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun dataInitialize(){

        requestsArrayList = arrayListOf<RequestsData>()

        names = arrayOf(
            "1",
            "2",
            "3",
            "4",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
        )


        for(i in names.indices){

            val requests = RequestsData(names[i])
            requestsArrayList.add(requests)
        }
    }
}
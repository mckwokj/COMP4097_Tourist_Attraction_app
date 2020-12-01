package edu.hkbu.comp.comp4097.comp4097project.ui.range

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import edu.hkbu.comp.comp4097.comp4097project.R
import edu.hkbu.comp.comp4097.comp4097project.data.AppDatabase
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceInfo
import edu.hkbu.comp.comp4097.comp4097project.ui.range.dummy.DummyContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class RangeFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_range_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val sharedPreferences = activity?.getSharedPreferences("placeInfo", Context.MODE_PRIVATE)
                var requestPage = sharedPreferences?.getString("requestPage", "")

                Log.d("log", "requestPage: ${requestPage}")

                if (requestPage == "distanceFragment"){
                val range = arguments?.getString("range")?.substring(0, 3)?.toInt()
                Log.d("log", "range: ${range}")
                if ( range != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = AppDatabase.getInstance(context).placeDao()
                        val place = dao.findXidByDist(range)
                        CoroutineScope(Dispatchers.Main).launch {
                            adapter = RangeRecyclerViewAdapter(place)
                        }
                    }
                    (activity as
                            AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                }else if (requestPage == "myPlace"){
                    var xidList: MutableList<String>? = null
                    val job = CoroutineScope(Dispatchers.IO).launch {
                            xidList = loadUserLike()
                        }
                    var placeList: MutableList<PlaceInfo>? = mutableListOf()

                    CoroutineScope(Dispatchers.IO).launch {
                        job.join()
                        Log.d("log", "xidList: ${xidList}")
                        val dao = AppDatabase.getInstance(context).placeDao()
                        if (xidList != null) {
                            xidList!!.forEach {
                                val place = dao.findPlaceByXid(it)
                                Log.d("log","place from findPlaceByXid: ${place} ")
                                placeList!!.add(place)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                adapter = RangeRecyclerViewAdapter(placeList!!)
                            }
                            (activity as
                                    AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                                true
                            )
                        }
                    }
                }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RangeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    suspend fun loadUserLike(): MutableList<String>? {
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
        var userName = sharedPreferences.getString("userName", "")

        val db = FirebaseFirestore.getInstance()
        var result: MutableList<String>? = null
        val docRef = db.collection("userLikeData").document(userName!!)
        var stopLoop = false

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    result = (document.get("xid") as MutableList<String>?)
                    Log.d("log", "DocumentSnapshot data: ${result}")
                    stopLoop = true
                } else {
                    Log.d("log", "No such document")
                    stopLoop = true
                }
            }
            .addOnFailureListener { exception ->
                Log.d("log", "get failed with ", exception)
                stopLoop = true
            }
        while (stopLoop == false) {
            delay(1)
        }

        return result
    }
}
package edu.hkbu.comp.comp4097.comp4097project.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.hkbu.comp.comp4097.comp4097project.Network
import edu.hkbu.comp.comp4097.comp4097project.R
import edu.hkbu.comp.comp4097.comp4097project.data.Place
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceDetail
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HomeFragment : Fragment() {

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
        val recyclerView = inflater.inflate(R.layout.fragment_home_list, container, false) as RecyclerView

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = HomeRecyclerViewAdapter(DummyContent.ITEMS)
//            }
//        }

//        CoroutineScope(Dispatchers.IO).launch {
//            Network.getCurrentLocation(requireContext())
//        }

        val pref: SharedPreferences = context?.getSharedPreferences(
            "placeInfo",
            Context.MODE_PRIVATE
        )!!

        var homeFragmentJson = pref.getString("homeFragmentJson", "")

        CoroutineScope(Dispatchers.IO).launch {
            // Need to extract from json to get the xid
            val place =
                Gson().fromJson<Place>(homeFragmentJson, object : TypeToken<Place>() {}.type)
            val isWiki: MutableList<String>? = mutableListOf()
//
            place.features.forEach {
                val detailInfoJson = Network.getDetailInfoById(it.properties.xid)
                val placeDetail = Gson().fromJson<PlaceDetail>(
                    detailInfoJson,
                    object : TypeToken<PlaceDetail>() {}.type
                )

//                if (placeDetail.info == null) {
//                    Log.d("placeDetail", placeDetail.xid)
//                } else {
//                    Log.d("placeDetailWiki", placeDetail.info.toString())
//                }

            }
        }

        if (homeFragmentJson != null) {
            Log.d("log", "homeFragmentJson: ${homeFragmentJson.toString()}")
            val place = Gson().fromJson<Place>(homeFragmentJson, object : TypeToken<Place>() {}.type)
            val placeList: MutableList<PlaceInfo>? = mutableListOf()

            Log.d("log", "placeList: ${placeList.toString()}")

            place.features.forEach {
//                Log.d("xid", it.properties.xid)
                placeList?.add(PlaceInfo(
                    name = it.properties.name,
                    coordinates = it.geometry.coordinates,
                    xid = it.properties.xid, dist = it.properties.dist, kinds = it.properties.kinds,
                    image_URL = null, district = null
                ))
            }
            recyclerView.adapter = placeList?.let { HomeRecyclerViewAdapter(it) }
        }

        return recyclerView
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
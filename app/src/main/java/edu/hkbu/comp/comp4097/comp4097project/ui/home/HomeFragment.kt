package edu.hkbu.comp.comp4097.comp4097project.ui.home

import android.app.Activity
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
import edu.hkbu.comp.comp4097.comp4097project.data.AppDatabase
import edu.hkbu.comp.comp4097.comp4097project.data.Place
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceDetail
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceInfo
import edu.hkbu.comp.comp4097.comp4097project.ui.load.LoadingDialog
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

        val recyclerView =
            inflater.inflate(R.layout.fragment_home_list, container, false) as RecyclerView

        val pref: SharedPreferences = context?.getSharedPreferences(
            "placeInfo",
            Context.MODE_PRIVATE
        )!!

        var homeFragmentJson = pref.getString("homeFragmentJson", "")

        if (homeFragmentJson != "") {
            CoroutineScope(Dispatchers.Main).launch {
                val loadingDialog = LoadingDialog(context as Activity)
                loadingDialog.startLoadingDialog()
                Log.d("log", "homeFragmentJson: ${homeFragmentJson.toString()}")
                val place =
                    Gson().fromJson<Place>(homeFragmentJson, object : TypeToken<Place>() {}.type)
                var placeList: List<PlaceInfo>? = listOf()
                var detailInfoMap: MutableMap<String, List<String>>? = mutableMapOf()

                var image_URL: String? = null
                var description: String? = null
                var district: String? = null

//            Log.d("log", "placeList: ${placeList.toString()}")

                // find image_URl, description and district
                val job = CoroutineScope(Dispatchers.IO).launch {

                    place.features.forEach {
                        val detailInfoJson = Network.getDetailInfoById(it.properties.xid)
                        val placeDetail = Gson().fromJson<PlaceDetail>(
                            detailInfoJson,
                            object : TypeToken<PlaceDetail>() {}.type
                        )

                        if (placeDetail?.preview != null) {
//                placeDetail.preview["source"]?.let { Log.d("preview", it) }
                            image_URL = placeDetail.preview["source"]
                        }

                        if (placeDetail?.wikipedia_extracts != null) {
                            placeDetail.wikipedia_extracts["text"]?.let { Log.d("preview", it) }
                            description = placeDetail.wikipedia_extracts["text"]
                        }

                        district = placeDetail?.address?.get("county")

                        // use regular expression to capture the english part
                        val pattern = "[A-Za-z\\s]+".toRegex()
                        val match = district?.let { pattern.find(it) }

                        district = match?.value

                        // place image_URL, description, district into a map whose element will be
                        // stored into PlaceInfo object
                        if (detailInfoMap != null) {
                            placeDetail.xid?.let { it1 ->
                                detailInfoMap.put(
                                    it1,
                                    listOf(image_URL, description, district) as List<String>
                                )
                            }
                            Log.d("map", detailInfoMap.toString())
                        }
                    }
                }

                val job2 = CoroutineScope(Dispatchers.IO).launch {
                    job.join()
                    val dao = AppDatabase.getInstance(requireContext()).placeDao()
                    place.features.forEach {
                        dao.insert(
                            PlaceInfo(
                                name = it.properties.name,
                                lat = it.geometry.coordinates[1],
                                lon = it.geometry.coordinates[0],
                                xid = it.properties.xid,
                                dist = it.properties.dist,
                                kinds = it.properties.kinds,
                                image_URL = detailInfoMap?.get(it.properties.xid)?.get(0),
                                description = detailInfoMap?.get(it.properties.xid)?.get(1),
                                district = detailInfoMap?.get(it.properties.xid)?.get(2),
                            )
                        )
                    }
                    placeList = dao.findAllPlaces()
                    Log.d("PlaceList", placeList.toString())
                }

                val job3 = CoroutineScope(Dispatchers.Main).launch {
                    job2.join()
                    recyclerView.adapter = placeList?.let { HomeRecyclerViewAdapter(it) }
                }
                job3.join()
                loadingDialog.dismissDialog()
            }
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
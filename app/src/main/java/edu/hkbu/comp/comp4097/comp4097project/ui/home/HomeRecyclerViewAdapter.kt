package edu.hkbu.comp.comp4097.comp4097project.ui.home

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import edu.hkbu.comp.comp4097.comp4097project.Network
import edu.hkbu.comp.comp4097.comp4097project.R
import edu.hkbu.comp.comp4097.comp4097project.data.*

import edu.hkbu.comp.comp4097.comp4097project.ui.home.dummy.DummyContent.DummyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files.size
import kotlin.math.round

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class HomeRecyclerViewAdapter(
    private val values: List<PlaceInfo>
) : RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_home_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        CoroutineScope(Dispatchers.Main).launch {
            // only show an attraction with an image
            if (item.image_URL != null) {
                Picasso.get().load(item.image_URL).into(holder.imageView)
                holder.placeText.text = item.name
                val roundedDistance = item.dist?.times(100)?.let { round(it) / 100 }.toString()
                holder.districtText.text =
                    "District:${item.district}\nApproximate distance: ${roundedDistance}m"
                holder.xid = item.xid
            }
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val placeText: TextView = view.findViewById(R.id.placeText)
        val districtText: TextView = view.findViewById(R.id.districtText)
        var xid: String? = null

        init {
            view.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_detailFragment,
                    // in the future, only send attraction id to fetch data from Database
                bundleOf(
                    Pair("xid", xid)
//                    Pair("placeText", placeText.text),
//                    Pair("imageURL", item.image_URL),
//                    Pair("districtText", districtText.text),
//                    Pair("description", "Description:")
                ))
            }
        }

//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
    }
}
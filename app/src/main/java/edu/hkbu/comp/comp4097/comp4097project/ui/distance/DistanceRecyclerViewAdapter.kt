package edu.hkbu.comp.comp4097.comp4097project.ui.distance

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import edu.hkbu.comp.comp4097.comp4097project.R

import edu.hkbu.comp.comp4097.comp4097project.ui.distance.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class DistanceRecyclerViewAdapter(
    private val values: List<String>
) : RecyclerView.Adapter<DistanceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_distance_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.findViewById(R.id.content)
        init {
            view.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_distanceFragment_to_rangeFragment,
                    bundleOf(Pair("range", contentView.text.toString()))
                )
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}
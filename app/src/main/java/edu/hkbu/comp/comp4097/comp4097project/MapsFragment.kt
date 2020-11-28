package edu.hkbu.comp.comp4097.comp4097project

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val name = arguments?.getString("name")

        if (lat != null && lon != null && name != null) {
            val place = LatLng(lat, lon)
            googleMap.addMarker(MarkerOptions().position(place).title(name))
            googleMap.setMinZoomPreference(15F)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(place))
        }

        val navBtn: Button = requireView().findViewById(R.id.navBtn)

        navBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose a navigation mode")
                .setItems(
                    arrayOf("Driving", "Walking", "Show street view"),
                    DialogInterface.OnClickListener { dialog, which ->
                        // The 'which' argument contains the index position
                        // of the selected item
                        var gmmIntentUri: Uri? = null

                        if (which == 0) {
//                                Log.d("Mode", "Driving is clicked")
                            gmmIntentUri =
                                Uri.parse("google.navigation:q=${lat},${lon}&mode=d")

                        } else if (which == 1){
//                                Log.d("Mode", "Walking is clicked")
                            gmmIntentUri =
                                Uri.parse("google.navigation:q=${lat},${lon}&mode=w")
                        } else {
                            gmmIntentUri = Uri.parse("google.streetview:cbll=${lat},${lon}")
                        }

                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps")

                        val packageManager = requireActivity().packageManager

                        // verify that at least one app that can handle the intent
                        mapIntent.resolveActivity(packageManager)?.let {
                            // Attempt to start an activity that can handle the Intent
                            startActivity(mapIntent)
                        }
                    })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    Log.d("Mode", "Cancel is clicked")
                    dialog.cancel()
                })
            builder.show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
package edu.hkbu.comp.comp4097.comp4097project

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.hkbu.comp.comp4097.comp4097project.data.AppDatabase
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceInfo
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val xid = arguments?.getString("xid", "")
        var place: PlaceInfo? = null

        val sharedPreferences: SharedPreferences? =
            context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var userName = sharedPreferences?.getString("userName", "")
        var loginState = sharedPreferences?.getString("loginState", "")

        val sharedPreferences2 = activity?.getSharedPreferences("placeInfo", Context.MODE_PRIVATE)
        var requestPage = sharedPreferences2?.getString("requestPage", "")

        val job = CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(requireContext()).placeDao()

            if (xid != null) {
                place = dao.findPlaceByXid(xid)
                Log.d("DetailFragment", place.toString())
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            job.join()
            val imageView: ImageView = view.findViewById(R.id.detailImageView)
            val placeTextView: TextView = view.findViewById(R.id.detailPlaceText)
            val descTextView: TextView = view.findViewById(R.id.detailDescText)
            val districtTextView: TextView = view.findViewById(R.id.detailDistrictText)

            val image_URL = place?.image_URL
            val attractionName = place?.name
            val districtText = place?.district
            val description = place?.description
            val roundedDistance = place?.dist?.times(100)?.let { round(it) / 100 }.toString()

            Picasso.get().load(image_URL).into(imageView)
            placeTextView.text = attractionName
            districtTextView.text = "District:${districtText}\nApproximate distance: " +
                    "${roundedDistance}m"
            descTextView.text = "Description:${description}"

            val mapBtn: Button = view.findViewById(R.id.mapBtn)
            val shareBtn: Button = view.findViewById(R.id.shareBtn)
            val calendarBtn: Button = view.findViewById(R.id.calendarBtn)
            val facilityBtn: Button = view.findViewById(R.id.facilityBtn)
            val saveBtn: Button = view.findViewById(R.id.saveBtn)

            var userSavedXid: MutableList<String>? = mutableListOf()

            if (loginState == "login") {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    userSavedXid = loadUserLike()
                }

                CoroutineScope(Dispatchers.Main).launch {
                    job.join()
                    if (userSavedXid != null) {
                        if (userSavedXid?.contains(xid)!!) {
                            Log.d("log", "xid contain in userSavedXid: ${xid}")
                            saveBtn.text = "REMOVE"
                        }
                    }
                }
            }

            saveBtn.setOnClickListener {
                if (saveBtn.text != "REMOVE") {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (loginState == "login") {
                            writeUserLike(xid!!)

                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(activity, "Adding successful!", Toast.LENGTH_SHORT)
                                    .show()
                                fragmentManager?.popBackStack()
                            }
                        }else{
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(activity, "Not yet login!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }else{
                    Log.d("log", "Remove btn click")
                    userSavedXid?.remove(xid)

                    val sharedPreferences: SharedPreferences =
                        context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
                    var userName = sharedPreferences.getString("userName", "")
                    val db = FirebaseFirestore.getInstance()

                    val field = hashMapOf(
                        "xid" to userSavedXid
                    )

                    db.collection("userLikeData").document(userName!!)
                        .set(field)
                        .addOnSuccessListener { Log.d("log", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("log", "Error writing document", e) }

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(activity, "Remove successful!", Toast.LENGTH_SHORT)
                            .show()
                        fragmentManager?.popBackStack()
                    }
                }
            }

            facilityBtn.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Choose a facility")
                    .setItems(
                        arrayOf("Restaurant", "Toilet", "Convenience store"),
                        DialogInterface.OnClickListener { dialog, which ->
                            // The 'which' argument contains the index position
                            // of the selected item
                            var gmmIntentUri: Uri? = null

                            if (which == 0) {
//                                Log.d("Mode", "Driving is clicked")
                                gmmIntentUri =
                                Uri.parse("geo:${place?.lat},${place?.lon}?q=restaurants")

                            } else if (which == 1){
//                                Log.d("Mode", "Walking is clicked")
                                gmmIntentUri =
                                    Uri.parse("geo:${place?.lat},${place?.lon}?q=toilets")
                            } else {
                                gmmIntentUri =
                                    Uri.parse("geo:${place?.lat},${place?.lon}?q=${Uri.encode("convenience store")}")
                            }

                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                            // Make the Intent explicit by setting the Google Maps package
                            mapIntent.setPackage("com.google.android.apps.maps")

                            val packageManager = requireActivity().packageManager

                            Log.d("packageManager", packageManager.toString())
                            // verify that at least one app that can handle the intent
//                            mapIntent.resolveActivity(packageManager)?.let {
                                // Attempt to start an activity that can handle the Intent
                                startActivity(mapIntent)
//                            }
                        })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        Log.d("Mode", "Cancel is clicked")
                        dialog.cancel()
                    })
                builder.show()
            }

            calendarBtn.setOnClickListener{
                val calendarIntent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "Visit ${place?.name}")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, place?.name)

                startActivity(calendarIntent)
            }

            shareBtn.setOnClickListener{
                val shareIntent: Intent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("text/plain")
                val shareSub = "Checkout this tourist attraction"
                val shareBody = "Place: ${place?.name}\nDistrict:${districtText}\n${descTextView.text}"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(shareIntent, "Share using"))
            }

            mapBtn.setOnClickListener {

                Log.d("coordinate", place?.lat.toString())
                Log.d("coordinate", place?.lon.toString())

                it.findNavController().navigate(R.id.action_detailFragment_to_mapsFragment2,
                bundleOf(Pair("lat", place?.lat), Pair("lon", place?.lon), Pair("name", place?.name)))

//                val builder = AlertDialog.Builder(context)
//                builder.setTitle("Choose a navigation mode")
//                    .setItems(
//                        arrayOf("Driving", "Walking"),
//                        DialogInterface.OnClickListener { dialog, which ->
//                            // The 'which' argument contains the index position
//                            // of the selected item
//                            var gmmIntentUri: Uri? = null
//
//                            if (which == 0) {
////                                Log.d("Mode", "Driving is clicked")
//                                gmmIntentUri =
//                                    Uri.parse("google.navigation:q=${place?.lat},${place?.lon}&mode=d")
//
//                            } else {
////                                Log.d("Mode", "Walking is clicked")
//                                gmmIntentUri =
//                                    Uri.parse("google.navigation:q=${place?.lat},${place?.lon}&mode=w")
//                            }
//
//                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//
//                            // Make the Intent explicit by setting the Google Maps package
//                            mapIntent.setPackage("com.google.android.apps.maps")
//
//                            val packageManager = requireActivity().packageManager
//
//                            // verify that at least one app that can handle the intent
//                            mapIntent.resolveActivity(packageManager)?.let {
//                                // Attempt to start an activity that can handle the Intent
//                                startActivity(mapIntent)
//                            }
//                        })
//                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
//                        Log.d("Mode", "Cancel is clicked")
//                        dialog.cancel()
//                    })
//                builder.show()
            }


//            if (image_URL != null) {
//                Log.d("DetailFragment", image_URL)
//            }
//            if (attractionName != null) {
//                Log.d("DetailFragment", attractionName)
//            }
//            if (districtText != null) {
//                Log.d("DetailFragment", districtText)
//            }
//            if (description != null) {
//                Log.d("DetailFragment", description)
//            }

//        if (attractionName != null) {
//            Log.d("DetailFragment", attractionName)
//        }
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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

    suspend fun writeUserLike(xidAdd: String) {
        val sharedPreferences: SharedPreferences =
            context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
        var userName = sharedPreferences.getString("userName", "")

        val db = FirebaseFirestore.getInstance()
        var newLikeXid: MutableList<String>? = null

        if (loadUserLike() != null) {
            newLikeXid = loadUserLike()!!
            newLikeXid.add(xidAdd)
            Log.d("log", "newLikeXid: ${newLikeXid}")
        } else {
            newLikeXid = mutableListOf(xidAdd)
            Log.d("log", "newLikeXid: ${newLikeXid}")
        }

        val field = hashMapOf(
            "xid" to newLikeXid
        )

        db.collection("userLikeData").document(userName!!)
            .set(field)
            .addOnSuccessListener { Log.d("log", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("log", "Error writing document", e) }
    }
}
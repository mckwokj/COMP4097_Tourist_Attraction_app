package edu.hkbu.comp.comp4097.comp4097project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.hkbu.comp.comp4097.comp4097project.data.Location
import edu.hkbu.comp.comp4097.comp4097project.data.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQ_CODE = 1000;
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.homeFragment, R.id.categoryFragment, R.id.districtFragment, R.id.loginFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        CoroutineScope(Dispatchers.IO).launch {
//            loadDistrictByCoor()
//            loadNearPlaceByCoor()
//            loadDetailInfoById()
//        }

        // initialize fused location client
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Network.getCurrentLocation(this)

    }

//    private fun getCurrentLocation() {
//        // checking location permission
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // request permission
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
//            return
//        }
//
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location ->
//                // getting the last known or current location
//                Log.d("log", "${location.latitude}")
//                Log.d("log", "${location.longitude}")
////                textView2.text = "Latitude: ${location.latitude}"
////                textView3.text = "Longitude: ${location.longitude}"
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed on getting current location",
//                    Toast.LENGTH_SHORT).show()
//            }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



//private suspend fun loadDistrictByCoor(){
//    var location: Location
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val json = Network.getDistrictByCoor()
//            Log.d("log", "getDistrictByCoor() called")
//            location = Gson().fromJson<Location>(json,object: TypeToken<Location>() {}.type)
//            Log.d("log", location.address.county)
//        } catch (e: Exception) {
//            Log.d("log", "Error in loadDistrictByCoor")
//        }
//    }
//}

//private suspend fun loadNearPlaceByCoor(){
//    var place : Place
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val json = Network.getNearPlaceByCoor()
//            Log.d("log", "getNearPlaceByCoor() called")
//            place = Gson().fromJson<Place>(json,object: TypeToken<Place>() {}.type)
//            Log.d("log", place.features[0].properties.xid)
//        } catch (e: Exception) {
//            Log.d("log", "Error in loadPlaceByCoor")
//        }
//    }
//}
//
//private suspend fun loadDetailInfoById(){
//    var placeDetail : PlaceDetails
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val json = Network.getDetailInfoById()
//            Log.d("log", "getDetailInfoById() called")
//            placeDetail = Gson().fromJson<PlaceDetails>(json,object: TypeToken<PlaceDetails>() {}.type)
//            Log.d("log", placeDetail.name)
//        } catch (e: Exception) {
//            Log.d("log", "Error in loadDetailInfoById")
//        }
//    }
//}





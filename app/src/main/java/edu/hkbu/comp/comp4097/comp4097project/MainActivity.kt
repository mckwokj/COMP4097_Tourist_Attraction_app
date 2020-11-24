package edu.hkbu.comp.comp4097.comp4097project

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.hkbu.comp.comp4097.comp4097project.data.Location
import edu.hkbu.comp.comp4097.comp4097project.data.Place
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

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

        CoroutineScope(Dispatchers.IO).launch {
//            loadDistrictByCoor()
//            loadNearPlaceByCoor()
            loadDetailInfoById()

        }

    }
}

private suspend fun loadDistrictByCoor(){
    var location: Location
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val json = Network.getDistrictByCoor()
            Log.d("log", "getDistrictByCoor() called")
            location = Gson().fromJson<Location>(json,object: TypeToken<Location>() {}.type)
            Log.d("log", location.address.county)
        } catch (e: Exception) {
            Log.d("log", "Error in loadDistrictByCoor")
        }
    }
}

private suspend fun loadNearPlaceByCoor(){
    var place : Place
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val json = Network.getNearPlaceByCoor()
            Log.d("log", "getNearPlaceByCoor() called")
            place = Gson().fromJson<Place>(json,object: TypeToken<Place>() {}.type)
            Log.d("log", place.features[0].properties.xid)
        } catch (e: Exception) {
            Log.d("log", "Error in loadPlaceByCoor")
        }
    }
}

private suspend fun loadDetailInfoById(){
    var placeDetail : PlaceDetails
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val json = Network.getDetailInfoById()
            Log.d("log", "getDetailInfoById() called")
            placeDetail = Gson().fromJson<PlaceDetails>(json,object: TypeToken<PlaceDetails>() {}.type)
            Log.d("log", placeDetail.name)
        } catch (e: Exception) {
            Log.d("log", "Error in loadDetailInfoById")
        }
    }
}





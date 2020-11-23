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
            loadPlaceNameByCoor()
        }

    }
}

private suspend fun loadPlaceNameByCoor(){
    var location: Location
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val json = Network.getNameByCoor()
            Log.d("log", "getNameByCoor() called")
            location = Gson().fromJson<Location>(json,object: TypeToken<Location>() {}.type)
            Log.d("log", location.address.county)
        } catch (e: Exception) {
            Log.d("log", "Error in loadPlaceNameByCoor")
        }
    }
}
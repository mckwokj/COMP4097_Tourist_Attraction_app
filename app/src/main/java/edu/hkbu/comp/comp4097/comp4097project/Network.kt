package edu.hkbu.comp.comp4097.comp4097project


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.hkbu.comp.comp4097.comp4097project.data.Place
import edu.hkbu.comp.comp4097.comp4097project.data.PlaceDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL

class Network {
    companion object {

        private val LOCATION_PERMISSION_REQ_CODE = 1000;
        private lateinit var fusedLocationClient: FusedLocationProviderClient

        fun getTextByUrl(): String {
            val builder = StringBuilder()
            val url =
                "https://us1.locationiq.com/v1/reverse.php?key=pk.8b6c10eb906a649ba2211f6bdb7a29d3&lat=22.392284&lon=113.96711&format=json"
            Log.d("log", "url: ${url}")
            val connection =
                URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "application/json")
            Log.d("log", "responseCode: " + connection.responseCode.toString())
            var data: Int = connection.inputStream.read()
            Log.d("log", "data getting")
            while (data != -1) {
                builder.append(data.toChar())
                data = connection.inputStream.read()
            }
            Log.d("log", "data: ${builder.toString()} ")
            return builder.toString()
        }

        fun getDistrictByCoor(): String {
            val builder = StringBuilder()
            val url =
                "https://us1.locationiq.com/v1/reverse.php?key=pk.8b6c10eb906a649ba2211f6bdb7a29d3&lat=22.392284&lon=113.96711&format=json"
            Log.d("log", "url: ${url}")
            val connection =
                URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "application/json")
            Log.d("log", "responseCode: " + connection.responseCode.toString())
            var data: Int = connection.inputStream.read()
            Log.d("log", "data getting")
            while (data != -1) {
                builder.append(data.toChar())
                data = connection.inputStream.read()
            }
            Log.d("log", "data: ${builder.toString()} ")
            return builder.toString()
        }

        fun getNearPlaceByCoor(radius: Int = 500, lat: Double, lon: Double, limit: Int = 5): String {
//        fun getNearPlaceByCoor(): String {
            val builder = StringBuilder()
            val url =
                "https://api.opentripmap.com/0.1/en/places/radius?radius=${radius}&lon=${lon}&lat=${lat}&country=HK&limit=${limit}&apikey=5ae2e3f221c38a28845f05b6865192295a39e19c4e9125c9e0d55d14"

            Log.d("log", "url: ${url}")
            val connection =
                URL(url).openConnection() as HttpURLConnection

//            connection.setRequestProperty("Accept", "application/json")
            Log.d("log", "responseCode: " + connection.responseCode.toString())
            var data: Int = connection.inputStream.read()
            Log.d("log", "data getting")
            while (data != -1) {
                builder.append(data.toChar())
                data = connection.inputStream.read()
            }
            Log.d("log", "data: ${builder.toString()} ")
            return builder.toString()
        }

        fun getDetailInfoById(xid: String): String {
            val builder = StringBuilder()
            val url =
                "https://api.opentripmap.com/0.1/en/places/xid/${xid}?&apikey=5ae2e3f221c38a28845f05b6865192295a39e19c4e9125c9e0d55d14"
            Log.d("log", "url: ${url}")
            val connection = URL(url).openConnection() as HttpURLConnection

//            connection.setRequestProperty("Accept", "application/json")
//            Log.d("log", "responseCode: " + connection.responseCode.toString())
            if (connection.responseCode == 200) {
                var data: Int = connection.inputStream.read()
//            Log.d("log", "data getting")
                while (data != -1) {
                    builder.append(data.toChar())
                    data = connection.inputStream.read()
                }
            }
//            Log.d("log", "data: ${builder.toString()} ")
            return builder.toString()
        }

        fun getCurrentLocation(context: Context) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            var coordinate: List<Double>? = null

            // checking location permission
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // request permission
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE
                )
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // getting the last known or current location
                    Log.d("log", "${location.latitude}")
                    Log.d("log", "${location.longitude}")

                    CoroutineScope(Dispatchers.IO).launch {
                        val currentLocation = listOf(location.latitude, location.longitude)
                        val json = Network.getNearPlaceByCoor(
                            lat = currentLocation[0],
                            lon = currentLocation[1]
                        )

//                        Log.d("json", json+"abc")

                        val pref: SharedPreferences = context?.getSharedPreferences(
                            "placeInfo",
                            Context.MODE_PRIVATE
                        )!!

                        pref.edit().apply {
                            putString("homeFragmentJson", json)
                        }.commit()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context, "Failed on getting current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
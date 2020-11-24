package edu.hkbu.comp.comp4097.comp4097project


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL

class Network {
    companion object {
        fun getTextByUrl(): String {
            val builder = StringBuilder()
            val url = "https://us1.locationiq.com/v1/reverse.php?key=pk.8b6c10eb906a649ba2211f6bdb7a29d3&lat=22.392284&lon=113.96711&format=json"
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
            val url = "https://us1.locationiq.com/v1/reverse.php?key=pk.8b6c10eb906a649ba2211f6bdb7a29d3&lat=22.392284&lon=113.96711&format=json"
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

        fun getNearPlaceByCoor(): String {
            val builder = StringBuilder()
            val url = "https://api.opentripmap.com/0.1/en/places/radius?radius=150&lon=113.96458&lat=22.38929&country=HK&apikey=5ae2e3f221c38a28845f05b6865192295a39e19c4e9125c9e0d55d14"
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

        fun getDetailInfoById(): String {
            val builder = StringBuilder()
            val url = "https://api.opentripmap.com/0.1/en/places/xid/4947449?&apikey=5ae2e3f221c38a28845f05b6865192295a39e19c4e9125c9e0d55d14"
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






    }
}
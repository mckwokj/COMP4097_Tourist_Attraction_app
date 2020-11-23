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
        fun getTextFromNetwork(): String {
            val builder = StringBuilder()
            val url = "def"
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

        private suspend fun reloadData(): List<Houses>{
            val HOUSES_URL = "https://morning-plains-00409.herokuapp.com/property/json"
            var houses = listOf<Houses>()
            var job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val json = Network.getTextFromNetwork(HOUSES_URL)
                    Log.d("Network",  "Json from: ${HOUSES_URL}")
                    Log.d("Network",  "Json content: ${json.toString()}")
                    houses = Gson().fromJson<List<Houses>>(json,object :
                        TypeToken<List<Houses>>() {}.type)
                } catch (e: Exception) {
                    Log.d("Network", "Error in loading data")
                    houses =
                        listOf(Houses("", "", "","Please check your network connection","Cannot fetch houses", "","","","","","",""))
                }
            }
            job.join()
            return houses
        }




    }
}
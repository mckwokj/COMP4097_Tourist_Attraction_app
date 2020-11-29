package edu.hkbu.comp.comp4097.comp4097project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [userFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class userFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val userView = inflater.inflate(R.layout.fragment_user, container, false)
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
        var loginState = sharedPreferences.getString("loginState", "")
        var userName = sharedPreferences.getString("userName", "")

        if (loginState == "login") {
            userView.userNameTextView.text = userName
        }else{
            userView.userNameTextView.text = "Not yet login"
        }

        userView.logBtn.setOnClickListener {
            if (loginState == "login") {
                it.findNavController().navigate(
                    R.id.action_userFragment_to_logoutFragment)
            }else{
                it.findNavController().navigate(
                    R.id.action_userFragment_to_loginFragment)
            }
        }

        userView.showUserBtn.setOnClickListener{
            Toast.makeText(activity, FirebaseAuth.getInstance().currentUser.toString(), Toast.LENGTH_SHORT).show()
        }

        userView.myPlaceBtn.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                loadUserLike()
            }

        }

        userView.addPlaceBtn.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                writeUserLike(5)
            }

        }


        return userView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment userFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            userFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    suspend fun loadUserLike(): MutableList<Int>?{
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
        var userName = sharedPreferences.getString("userName", "")

        val db = FirebaseFirestore.getInstance()
        var result : MutableList<Int>? = null
        val docRef = db.collection("userLikeData").document(userName!!)
        var stopLoop = false

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        result = (document.get("xid") as MutableList<Int>?)!!
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

        while (stopLoop == false){
            delay(1)
        }

        return result
    }

        suspend fun writeUserLike(xidAdd: Int){
        val sharedPreferences: SharedPreferences = context?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!
        var userName = sharedPreferences.getString("userName", "")

        val db = FirebaseFirestore.getInstance()

        var newLikeXid:MutableList<Int> = loadUserLike()!!
        newLikeXid.add(xidAdd)
        Log.d("log", "newLikeXid: ${newLikeXid}")

        val field = hashMapOf(
            "xid" to newLikeXid
        )

        db.collection("userLikeData").document(userName!!)
            .set(field)
            .addOnSuccessListener { Log.d("log", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("log", "Error writing document", e) }
    }


}
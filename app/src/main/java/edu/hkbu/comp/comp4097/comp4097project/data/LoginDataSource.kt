package edu.hkbu.comp.comp4097.comp4097project.data

import com.google.firebase.auth.FirebaseAuth
import edu.hkbu.comp.comp4097.comp4097project.data.model.LoggedInUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val mAuth = FirebaseAuth.getInstance()

    suspend fun login(username: String, password: String): Result<LoggedInUser> {

        val resultChannel = Channel<Result<LoggedInUser>>()
        mAuth.signInWithEmailAndPassword(username, password).addOnSuccessListener { authResult ->
            authResult?.user?.let { user ->
                val u = LoggedInUser(user.uid, user.email!!)
                MainScope().launch { resultChannel.send(Result.Success(u)) }
            }
        }.addOnFailureListener {e ->
            MainScope().launch { resultChannel.send(Result.Error(IOException("Error logging in", e))) }
        }
        return resultChannel.receive()
//        try {
//            // TODO: handle loggedInUser authentication
//            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
//        } catch (e: Throwable) {
//            return Result.Error(IOException("Error logging in", e))
//        }
    }

    fun logout() {
        mAuth.signOut()
    }
}
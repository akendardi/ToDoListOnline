package com.example.todolistonline.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todolistonline.domain.FirebaseRepository
import com.example.todolistonline.domain.states.LoginState
import com.example.todolistonline.domain.states.RegistrationState
import com.example.todolistonline.domain.states.ResetPasswordState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context) :
    FirebaseRepository {


    override suspend fun createNewAccount(
        email: String,
        password: String,
        name: String
    ): RegistrationState {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    ?.await()
                RegistrationState.Successful
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                RegistrationState.InvalidEmail
            } catch (e: FirebaseAuthUserCollisionException) {
                RegistrationState.UserCollision
            } catch (e: IllegalArgumentException) {
                RegistrationState.Error(e)
            }
        }
    }

    override suspend fun loginInAccount(email: String, password: String): LoginState {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                LoginState.Successful
            }  catch (e: FirebaseAuthInvalidCredentialsException) {
                LoginState.InvalidData
            }  catch (e: Exception) {
                LoginState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    override suspend fun resetPassword(email: String): ResetPasswordState {
        return withContext(Dispatchers.IO) {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                ResetPasswordState.Successful
            } catch (e: FirebaseAuthInvalidUserException) {
                ResetPasswordState.InvalidUser
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                ResetPasswordState.InvalidEmail
            } catch (e: Exception) {
                ResetPasswordState.Error(e.message ?: "Unknown error")
            }
        }
    }

    override fun checkConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }



    override fun logoutOfAccount() {
        auth.signOut()
    }
}
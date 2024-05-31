package com.example.todolistonline.data.repositories

import android.util.Log
import com.example.todolistonline.domain.FirebaseRepository
import com.example.todolistonline.domain.RegistrationState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) :
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
            } catch (e: FirebaseAuthUserCollisionException){
                RegistrationState.UserCollision
            } catch (e: IllegalArgumentException){
                RegistrationState.Error(e)
            }
        }
    }

    override fun loginInAccount(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(email: String) {
        TODO("Not yet implemented")
    }

    override fun logoutOfAccount() {
        TODO("Not yet implemented")
    }
}
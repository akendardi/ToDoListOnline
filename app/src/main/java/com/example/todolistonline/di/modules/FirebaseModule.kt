package com.example.todolistonline.di.modules

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database("https://todolist-48a69-default-rtdb.europe-west1.firebasedatabase.app")
    }
}
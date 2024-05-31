package com.example.todolistonline.di

import android.content.Context
import com.example.todolistonline.data.repositories.FirebaseRepositoryImpl
import com.example.todolistonline.di.modules.FirebaseModule
import com.example.todolistonline.di.modules.RepositoriesModule
import com.example.todolistonline.di.modules.ViewModelModule
import com.example.todolistonline.presentation.registration.RegistrationActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [FirebaseModule::class,
        RepositoriesModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun inject(activity: RegistrationActivity)

    fun inject(repositoryImpl: FirebaseRepositoryImpl)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }


}
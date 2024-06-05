package com.example.todolistonline.di

import android.app.Application
import android.content.Context
import com.example.todolistonline.data.repositories.FirebaseRepositoryImpl
import com.example.todolistonline.di.modules.AppSubcomponent
import com.example.todolistonline.di.modules.DatabaseModule
import com.example.todolistonline.di.modules.FirebaseModule
import com.example.todolistonline.di.modules.RepositoriesModule
import com.example.todolistonline.di.modules.ViewModelModule
import com.example.todolistonline.presentation.hello_activity.HelloActivity
import com.example.todolistonline.presentation.login.LoginActivity
import com.example.todolistonline.presentation.main.MainActivity
import com.example.todolistonline.presentation.main.fragments.TaskFragment
import com.example.todolistonline.presentation.registration.RegistrationActivity
import com.example.todolistonline.presentation.reset_password.ResetPasswordActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [FirebaseModule::class,
        RepositoriesModule::class,
        ViewModelModule::class,
        DatabaseModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(activity: RegistrationActivity)
    fun inject(fragment: TaskFragment)

    fun inject(repositoryImpl: FirebaseRepositoryImpl)

    fun inject(helloActivity: HelloActivity)

    fun inject(resetPasswordActivity: ResetPasswordActivity)

    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)

    fun appSubComponent(): AppSubcomponent.Factory

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context,
            @BindsInstance application: Application
        ): AppComponent
    }


}
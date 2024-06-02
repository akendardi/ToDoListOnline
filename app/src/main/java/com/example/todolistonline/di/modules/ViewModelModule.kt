package com.example.todolistonline.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolistonline.di.ViewModelKey
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.login.LoginViewModel
import com.example.todolistonline.presentation.registration.RegistrationViewModel
import com.example.todolistonline.presentation.reset_password.ResetPasswordViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    fun bindRegistrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel::class)
    fun bindResetPasswordViewModel(resetPasswordViewModel: ResetPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
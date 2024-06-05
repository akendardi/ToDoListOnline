package com.example.todolistonline.di.modules

import androidx.fragment.app.FragmentActivity
import com.example.todolistonline.presentation.main.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface AppSubcomponent {

    @Subcomponent.Factory
    interface Factory{
        fun create(
            @BindsInstance fragmentActivity: FragmentActivity
        ): AppSubcomponent
    }

    fun inject(mainActivity: MainActivity)
}
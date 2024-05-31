package com.example.todolistonline.di.modules

import com.example.todolistonline.data.repositories.FirebaseRepositoryImpl
import com.example.todolistonline.domain.FirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface RepositoriesModule {
    @Binds
    fun provideFirebaseRepositoryImpl(repositoryImpl: FirebaseRepositoryImpl): FirebaseRepository
}
package com.example.to_dolist.di

import com.example.to_dolist.data.localDatabase.TodoListDao
import com.example.to_dolist.data.network.ApiService
import com.example.to_dolist.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTodoRepository(apiService: ApiService, todoListDAO: TodoListDao): Repository {
        return Repository(apiService, todoListDAO)
    }
}
package com.example.to_dolist.di

import android.content.Context
import androidx.room.Room
import com.example.to_dolist.data.localDatabase.TodoListDatabase
import com.example.to_dolist.utils.Constants
import com.example.to_dolist.utils.TodoConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext app: Context,
    ) = Room.databaseBuilder(
        app,
        TodoListDatabase::class.java,
        Constants.LOCAL_DB_NAME
    )
        .fallbackToDestructiveMigration()
        .addTypeConverter(TodoConverter())
        .build()

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoListDatabase) = db.toDoDao()
}
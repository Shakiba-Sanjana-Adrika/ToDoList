package com.example.to_dolist.data.localDatabase


import androidx.room.*
import com.example.to_dolist.utils.TodoConverter

@Database(entities = [TodoListItem::class], version = 1)
@TypeConverters(TodoConverter::class)
abstract class TodoListDatabase : RoomDatabase() {

    abstract fun toDoDao(): TodoListDao
}

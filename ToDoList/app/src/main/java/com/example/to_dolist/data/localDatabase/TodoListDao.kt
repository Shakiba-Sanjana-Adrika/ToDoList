package com.example.to_dolist.data.localDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTodoList(todoList: TodoListItem)

    @Update
    fun updateTodoList(todoList: TodoListItem)

    @Delete
    fun deleteTodoList(todoList: TodoListItem)

    @Query("SELECT * FROM todoList order by time ASC")
    fun getTodoList(): LiveData<List<TodoListItem>>

    @Query("DELETE FROM todoList WHERE id LIKE :id")
    fun deleteById(id: String)

    @Query("SELECT * FROM todoList WHERE id LIKE :id")
    fun getTodoById(id: String): TodoListItem

    @Transaction
    @Query("SELECT * FROM todoList ORDER BY time ASC LIMIT 1")
    fun getAboveTodo(): TodoListItem
}
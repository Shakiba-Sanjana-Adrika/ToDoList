package com.example.to_dolist.data.repository

import androidx.lifecycle.LiveData
import com.example.to_dolist.data.localDatabase.TodoListDao
import com.example.to_dolist.data.localDatabase.TodoListItem
import com.example.to_dolist.data.network.ApiService
import com.example.to_dolist.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val todoListDao: TodoListDao,
) {
    /**
     * Insert data from remote server to local database
     */
    suspend fun insertTodoFromApiToLocalDB() {
        val result = apiService.todoList()
        for (todoItem in result) {
            val timeInMili = SimpleDateFormat(Constants.TIME_PATTERN).parse(todoItem.time).time
            if (timeInMili <= Calendar.getInstance().timeInMillis)
                continue
            todoListDao.insertTodoList(TodoListItem(
                todoItem.id,
                timeInMili,
                todoItem.title,
                todoItem.todo)
            )
        }
    }

    suspend fun insertTodoList(todoList: TodoListItem) {
        todoListDao.insertTodoList(todoList)
    }

    suspend fun deleteTodoList(todoList: TodoListItem) {
        todoListDao.deleteTodoList(todoList)
    }

    suspend fun updateTodoList(todoList: TodoListItem) {
        todoListDao.updateTodoList(todoList)
    }

    suspend fun deleteById(id: String) {
        todoListDao.deleteById(id)
    }

    fun getAllData(): LiveData<List<TodoListItem>> {
        return todoListDao.getTodoList()
    }

    suspend fun getTodoById(id: String): TodoListItem {
        return todoListDao.getTodoById(id)
    }

    suspend fun getAboveTodo(): TodoListItem {
        return todoListDao.getAboveTodo()
    }

}

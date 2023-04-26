package com.example.to_dolist.data.network

import com.example.to_dolist.data.model.TodoArrayList
import retrofit2.http.GET

interface ApiService {
    @GET(ApiUrl.EXTENSION_URL)
    suspend fun todoList(): TodoArrayList
}
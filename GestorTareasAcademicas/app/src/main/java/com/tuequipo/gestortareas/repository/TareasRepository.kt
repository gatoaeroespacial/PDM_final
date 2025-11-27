package com.tuequipo.gestortareas.repository

import com.tuequipo.gestortareas.data.*
import com.tuequipo.gestortareas.network.RetrofitClient

class TareasRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun register(nombre: String, email: String, password: String) =
        apiService.register(RegisterRequest(nombre, email, password))

    suspend fun login(email: String, password: String) =
        apiService.login(LoginRequest(email, password))

    suspend fun logout(token: String) =
        apiService.logout("Bearer $token")

    suspend fun getTareas(token: String) =
        apiService.getTareas("Bearer $token")

    suspend fun getTarea(token: String, id: Int) =
        apiService.getTarea("Bearer $token", id)

    suspend fun createTarea(token: String, tarea: TareaRequest) =
        apiService.createTarea("Bearer $token", tarea)

    suspend fun updateTarea(token: String, id: Int, tarea: TareaRequest) =
        apiService.updateTarea("Bearer $token", id, tarea)

    suspend fun deleteTarea(token: String, id: Int) =
        apiService.deleteTarea("Bearer $token", id)
}
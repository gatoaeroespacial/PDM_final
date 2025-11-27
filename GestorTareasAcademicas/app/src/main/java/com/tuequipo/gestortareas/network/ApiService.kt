package com.tuequipo.gestortareas.network

import com.tuequipo.gestortareas.data.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<MessageResponse>

    @GET("tareas")
    suspend fun getTareas(@Header("Authorization") token: String): Response<List<Tarea>>

    @GET("tareas/{id}")
    suspend fun getTarea(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Tarea>

    @POST("tareas")
    suspend fun createTarea(
        @Header("Authorization") token: String,
        @Body request: TareaRequest
    ): Response<TareaResponse>

    @PUT("tareas/{id}")
    suspend fun updateTarea(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: TareaRequest
    ): Response<TareaResponse>

    @DELETE("tareas/{id}")
    suspend fun deleteTarea(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MessageResponse>
}
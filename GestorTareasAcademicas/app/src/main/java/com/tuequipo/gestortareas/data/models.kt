package com.tuequipo.gestortareas.data

import com.google.gson.annotations.SerializedName

// Request para registro
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)

// Request para login
data class LoginRequest(
    val email: String,
    val password: String
)

// Response de login
data class LoginResponse(
    val message: String,
    val token: String,
    val userId: Int,
    val nombre: String,
    val email: String
)

// Response genérico con mensaje
data class MessageResponse(
    val message: String
)

// Modelo de Tarea
data class Tarea(
    val id: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int = 0,
    val titulo: String,
    val descripcion: String? = null,
    val materia: String? = null,
    @SerializedName("fecha_entrega") val fechaEntrega: String? = null,
    val prioridad: String = "media",
    val completada: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

// Request para crear/actualizar tarea
data class TareaRequest(
    val titulo: String,
    val descripcion: String? = null,
    val materia: String? = null,
    @SerializedName("fecha_entrega") val fechaEntrega: String? = null,
    val prioridad: String = "media",
    val completada: Boolean = false
)

// Response al crear tarea
data class TareaResponse(
    val message: String,
    val tarea: Tarea
)
package com.tuequipo.gestortareas.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuequipo.gestortareas.databinding.ActivityRegisterBinding
import com.tuequipo.gestortareas.repository.TareasRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var repository: TareasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TareasRepository()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInputs(nombre, email, password, confirmPassword)) {
                register(nombre, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(
        nombre: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (nombre.isEmpty()) {
            binding.etNombre.error = "El nombre es requerido"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "El email es requerido"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email inválido"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "La contraseña es requerida"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }

        return true
    }

    private fun register(nombre: String, email: String, password: String) {
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = repository.register(nombre, email, password)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registro exitoso. Ahora puedes iniciar sesión",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnRegister.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.btnRegister.isEnabled = true
            }
        }
    }
}
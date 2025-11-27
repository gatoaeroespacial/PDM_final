package com.tuequipo.gestortareas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuequipo.gestortareas.databinding.ActivityLoginBinding
import com.tuequipo.gestortareas.repository.TareasRepository
import com.tuequipo.gestortareas.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: TareasRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TareasRepository()
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (validateInputs(email, password)) {
                login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
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

        return true
    }

    private fun login(email: String, password: String) {
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = repository.login(email, password)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    sessionManager.saveSession(
                        loginResponse.token,
                        loginResponse.userId,
                        loginResponse.nombre,
                        loginResponse.email
                    )
                    Toast.makeText(this@LoginActivity, "Bienvenido ${loginResponse.nombre}", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
package com.tuequipo.gestortareas.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuequipo.gestortareas.R
import com.tuequipo.gestortareas.databinding.ActivityMainBinding
import com.tuequipo.gestortareas.data.Tarea
import com.tuequipo.gestortareas.repository.TareasRepository
import com.tuequipo.gestortareas.ui.adapters.TareasAdapter
import com.tuequipo.gestortareas.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: TareasRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: TareasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TareasRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        loadTareas()
    }

    override fun onResume() {
        super.onResume()
        loadTareas()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mis Tareas"
        supportActionBar?.subtitle = sessionManager.getUserName()
    }

    private fun setupRecyclerView() {
        adapter = TareasAdapter(
            onItemClick = { tarea ->
                val intent = Intent(this, TareaDetailActivity::class.java)
                intent.putExtra("TAREA_ID", tarea.id)
                startActivity(intent)
            },
            onDeleteClick = { tarea ->
                showDeleteConfirmation(tarea)
            },
            onCompleteClick = { tarea ->
                toggleTareaComplete(tarea)
            }
        )

        binding.rvTareas.layoutManager = LinearLayoutManager(this)
        binding.rvTareas.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddTarea.setOnClickListener {
            val intent = Intent(this, TareaDetailActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadTareas()
        }
    }

    private fun loadTareas() {
        val token = sessionManager.getToken() ?: return

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                val response = repository.getTareas(token)

                if (response.isSuccessful && response.body() != null) {
                    val tareas = response.body()!!
                    adapter.submitList(tareas)

                    if (tareas.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvTareas.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvTareas.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al cargar tareas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun toggleTareaComplete(tarea: Tarea) {
        val token = sessionManager.getToken() ?: return

        lifecycleScope.launch {
            try {
                val request = com.tuequipo.gestortareas.data.TareaRequest(
                    titulo = tarea.titulo,
                    descripcion = tarea.descripcion,
                    materia = tarea.materia,
                    fechaEntrega = tarea.fechaEntrega,
                    prioridad = tarea.prioridad,
                    completada = !tarea.completada
                )

                val response = repository.updateTarea(token, tarea.id, request)

                if (response.isSuccessful) {
                    loadTareas()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmation(tarea: Tarea) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar tarea")
            .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteTarea(tarea)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteTarea(tarea: Tarea) {
        val token = sessionManager.getToken() ?: return

        lifecycleScope.launch {
            try {
                val response = repository.deleteTarea(token, tarea.id)

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                    loadTareas()
                } else {
                    Toast.makeText(this@MainActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                sessionManager.clearSession()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
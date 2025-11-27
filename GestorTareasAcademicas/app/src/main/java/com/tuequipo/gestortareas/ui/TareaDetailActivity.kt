package com.tuequipo.gestortareas.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuequipo.gestortareas.R
import com.tuequipo.gestortareas.databinding.ActivityTareaDetailBinding
import com.tuequipo.gestortareas.data.Tarea
import com.tuequipo.gestortareas.data.TareaRequest
import com.tuequipo.gestortareas.repository.TareasRepository
import com.tuequipo.gestortareas.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TareaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTareaDetailBinding
    private lateinit var repository: TareasRepository
    private lateinit var sessionManager: SessionManager
    private var tareaId: Int? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = TareasRepository()
        sessionManager = SessionManager(this)

        tareaId = intent.getIntExtra("TAREA_ID", -1).takeIf { it != -1 }

        setupToolbar()
        setupPrioridadSpinner()
        setupListeners()

        if (tareaId != null) {
            loadTarea()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (tareaId != null) "Editar Tarea" else "Nueva Tarea"
    }

    private fun setupPrioridadSpinner() {
        val prioridades = arrayOf("baja", "media", "alta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, prioridades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrioridad.adapter = adapter
        binding.spinnerPrioridad.setSelection(1) // media por defecto
    }

    private fun setupListeners() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveTarea()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = formatter.format(date.time)

            val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvSelectedDate.text = displayFormatter.format(date.time)
            binding.tvSelectedDate.visibility = View.VISIBLE
        }, year, month, day).show()
    }

    private fun loadTarea() {
        val token = sessionManager.getToken() ?: return
        val id = tareaId ?: return

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = repository.getTarea(token, id)

                if (response.isSuccessful && response.body() != null) {
                    val tarea = response.body()!!
                    fillForm(tarea)
                } else {
                    Toast.makeText(this@TareaDetailActivity, "Error al cargar tarea", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TareaDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun fillForm(tarea: Tarea) {
        binding.etTitulo.setText(tarea.titulo)
        binding.etDescripcion.setText(tarea.descripcion)
        binding.etMateria.setText(tarea.materia)

        val prioridadIndex = when (tarea.prioridad) {
            "baja" -> 0
            "media" -> 1
            "alta" -> 2
            else -> 1
        }
        binding.spinnerPrioridad.setSelection(prioridadIndex)

        if (tarea.fechaEntrega != null) {
            selectedDate = tarea.fechaEntrega
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = formatter.parse(tarea.fechaEntrega)

                val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvSelectedDate.text = displayFormatter.format(date)
                binding.tvSelectedDate.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveTarea() {
        val titulo = binding.etTitulo.text.toString().trim()

        if (titulo.isEmpty()) {
            binding.etTitulo.error = "El título es requerido"
            return
        }

        val descripcion = binding.etDescripcion.text.toString().trim().ifEmpty { null }
        val materia = binding.etMateria.text.toString().trim().ifEmpty { null }
        val prioridad = binding.spinnerPrioridad.selectedItem.toString()

        val tareaRequest = TareaRequest(
            titulo = titulo,
            descripcion = descripcion,
            materia = materia,
            fechaEntrega = selectedDate,
            prioridad = prioridad
        )

        val token = sessionManager.getToken() ?: return
        binding.btnSave.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = if (tareaId != null) {
                    repository.updateTarea(token, tareaId!!, tareaRequest)
                } else {
                    repository.createTarea(token, tareaRequest)
                }

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@TareaDetailActivity,
                        if (tareaId != null) "Tarea actualizada" else "Tarea creada",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(this@TareaDetailActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                    binding.btnSave.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@TareaDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
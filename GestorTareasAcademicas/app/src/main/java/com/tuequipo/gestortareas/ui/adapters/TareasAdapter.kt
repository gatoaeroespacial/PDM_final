package com.tuequipo.gestortareas.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tuequipo.gestortareas.R
import com.tuequipo.gestortareas.databinding.ItemTareaBinding
import com.tuequipo.gestortareas.data.Tarea

class TareasAdapter(
    private val onItemClick: (Tarea) -> Unit,
    private val onDeleteClick: (Tarea) -> Unit,
    private val onCompleteClick: (Tarea) -> Unit
) : ListAdapter<Tarea, TareasAdapter.TareaViewHolder>(TareaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TareaViewHolder(
        private val binding: ItemTareaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tarea: Tarea) {
            binding.tvTitulo.text = tarea.titulo
            binding.tvMateria.text = tarea.materia ?: "Sin materia"
            binding.tvFecha.text = tarea.fechaEntrega ?: "Sin fecha"

            // Prioridad
            val prioridadColor = when (tarea.prioridad) {
                "alta" -> R.color.red_500
                "media" -> R.color.orange_500
                else -> R.color.green_500
            }
            binding.viewPrioridad.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, prioridadColor)
            )

            // Estado completada
            binding.cbCompleted.isChecked = tarea.completada
            if (tarea.completada) {
                binding.tvTitulo.paintFlags = binding.tvTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTitulo.alpha = 0.5f
            } else {
                binding.tvTitulo.paintFlags = binding.tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTitulo.alpha = 1.0f
            }

            // Listeners
            binding.root.setOnClickListener { onItemClick(tarea) }
            binding.btnDelete.setOnClickListener { onDeleteClick(tarea) }
            binding.cbCompleted.setOnClickListener { onCompleteClick(tarea) }
        }
    }

    class TareaDiffCallback : DiffUtil.ItemCallback<Tarea>() {
        override fun areItemsTheSame(oldItem: Tarea, newItem: Tarea): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tarea, newItem: Tarea): Boolean {
            return oldItem == newItem
        }
    }
}
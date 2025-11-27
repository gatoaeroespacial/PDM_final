const db = require('../config/database');

// Obtener todas las tareas del usuario
exports.getTareas = async (req, res) => {
  try {
    const userId = req.userId;

    const [tareas] = await db.query(
      'SELECT * FROM tareas WHERE usuario_id = ? ORDER BY fecha_entrega ASC, created_at DESC',
      [userId]
    );

    res.json(tareas);
  } catch (error) {
    console.error('Error al obtener tareas:', error);
    res.status(500).json({ error: 'Error al obtener tareas' });
  }
};

// Obtener una tarea específica
exports.getTarea = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const [tareas] = await db.query(
      'SELECT * FROM tareas WHERE id = ? AND usuario_id = ?',
      [id, userId]
    );

    if (tareas.length === 0) {
      return res.status(404).json({ error: 'Tarea no encontrada' });
    }

    res.json(tareas[0]);
  } catch (error) {
    console.error('Error al obtener tarea:', error);
    res.status(500).json({ error: 'Error al obtener tarea' });
  }
};

// Crear nueva tarea
exports.createTarea = async (req, res) => {
  try {
    const userId = req.userId;
    const { titulo, descripcion, materia, fecha_entrega, prioridad } = req.body;

    // Validación básica
    if (!titulo) {
      return res.status(400).json({ error: 'El título es requerido' });
    }

    const [result] = await db.query(
      'INSERT INTO tareas (usuario_id, titulo, descripcion, materia, fecha_entrega, prioridad) VALUES (?, ?, ?, ?, ?, ?)',
      [userId, titulo, descripcion || null, materia || null, fecha_entrega || null, prioridad || 'media']
    );

    const [newTarea] = await db.query('SELECT * FROM tareas WHERE id = ?', [result.insertId]);

    res.status(201).json({
      message: 'Tarea creada exitosamente',
      tarea: newTarea[0]
    });
  } catch (error) {
    console.error('Error al crear tarea:', error);
    res.status(500).json({ error: 'Error al crear tarea' });
  }
};

// Actualizar tarea
exports.updateTarea = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;
    const { titulo, descripcion, materia, fecha_entrega, prioridad, completada } = req.body;

    // Verificar que la tarea existe y pertenece al usuario
    const [tareas] = await db.query(
      'SELECT * FROM tareas WHERE id = ? AND usuario_id = ?',
      [id, userId]
    );

    if (tareas.length === 0) {
      return res.status(404).json({ error: 'Tarea no encontrada' });
    }

    // Actualizar tarea
    await db.query(
      'UPDATE tareas SET titulo = ?, descripcion = ?, materia = ?, fecha_entrega = ?, prioridad = ?, completada = ? WHERE id = ?',
      [
        titulo || tareas[0].titulo,
        descripcion !== undefined ? descripcion : tareas[0].descripcion,
        materia !== undefined ? materia : tareas[0].materia,
        fecha_entrega !== undefined ? fecha_entrega : tareas[0].fecha_entrega,
        prioridad || tareas[0].prioridad,
        completada !== undefined ? completada : tareas[0].completada,
        id
      ]
    );

    const [updatedTarea] = await db.query('SELECT * FROM tareas WHERE id = ?', [id]);

    res.json({
      message: 'Tarea actualizada exitosamente',
      tarea: updatedTarea[0]
    });
  } catch (error) {
    console.error('Error al actualizar tarea:', error);
    res.status(500).json({ error: 'Error al actualizar tarea' });
  }
};

// Eliminar tarea
exports.deleteTarea = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    // Verificar que la tarea existe y pertenece al usuario
    const [tareas] = await db.query(
      'SELECT * FROM tareas WHERE id = ? AND usuario_id = ?',
      [id, userId]
    );

    if (tareas.length === 0) {
      return res.status(404).json({ error: 'Tarea no encontrada' });
    }

    await db.query('DELETE FROM tareas WHERE id = ?', [id]);

    res.json({ message: 'Tarea eliminada exitosamente' });
  } catch (error) {
    console.error('Error al eliminar tarea:', error);
    res.status(500).json({ error: 'Error al eliminar tarea' });
  }
};

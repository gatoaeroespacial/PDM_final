const express = require('express');
const router = express.Router();
const tareasController = require('../controllers/tareasController');
const authenticateToken = require('../middleware/auth');

// Todas las rutas de tareas requieren autenticación
router.use(authenticateToken);

// Rutas CRUD
router.get('/tareas', tareasController.getTareas);
router.get('/tareas/:id', tareasController.getTarea);
router.post('/tareas', tareasController.createTarea);
router.put('/tareas/:id', tareasController.updateTarea);
router.delete('/tareas/:id', tareasController.deleteTarea);

module.exports = router;
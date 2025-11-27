const db = require('../config/database');

const authenticateToken = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
      return res.status(401).json({ error: 'Token no proporcionado' });
    }

    // Verificar token en la base de datos
    const [sessions] = await db.query(
      'SELECT usuario_id FROM sesiones WHERE token = ?',
      [token]
    );

    if (sessions.length === 0) {
      return res.status(403).json({ error: 'Token inválido o expirado' });
    }

    req.userId = sessions[0].usuario_id;
    next();
  } catch (error) {
    console.error('Error en autenticación:', error);
    res.status(500).json({ error: 'Error en la autenticación' });
  }
};

module.exports = authenticateToken;

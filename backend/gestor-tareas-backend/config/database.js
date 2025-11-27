const mysql = require('mysql2');

const pool = mysql.createPool({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '12345',
  database: process.env.DB_NAME || 'gestor_tareas',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

const promisePool = pool.promise();

promisePool.query('SELECT 1')
  .then(() => {
    console.log('✅ Conexión a MySQL exitosa');
  })
  .catch((err) => {
    console.error('❌ Error al conectar a MySQL:', err.message);
  });

module.exports = promisePool;

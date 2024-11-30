const mysql = require('mysql');

const db = mysql.createConnection({
  host: '34.101.233.242',
  user: 'root',
  password: '123',
  database: 'auth_db', //ganti nama db nya
});

db.connect((err) => {
  if (err) {
    console.error('Database connection error:', err.message);
    return;
  }
  console.log('Connected to MySQL database.');
});

module.exports = db;
const mysql = require('mysql');

const db = mysql.createConnection({
  host: '',
  user: '',
  password: '',
  database: '',
});

db.connect((err) => {
  if (err) {
    console.error('Database connection error:', err.message);
    return;
  }
  console.log('Connected to MySQL database.');
});

module.exports = db;
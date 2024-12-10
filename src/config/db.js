/* eslint-disable linebreak-style */
const mysql = require('mysql');
const util = require('util');
require('dotenv').config();

const db = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME, //ganti nama db nya
});

db.connect((err) => {
  if (err) {
    console.error('Database connection error:', err.message);
    return;
  }
  console.log('Connected to MySQL database.');
});
db.query = util.promisify(db.query);
module.exports = db;
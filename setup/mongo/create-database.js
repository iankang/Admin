// Switch to the admin database
db = db.getSiblingDB('admin');

// Authenticate as the admin user
db.auth('admin', 'password');

// Create the ThinkDB database
db = db.getSiblingDB('ThinkDB');

db.auth('ian','point90');
// Create the admin user
db.createUser({
  user: "admin",
  pwd: "password",
  roles: [{ role: "root", db: "admin" }]
});

// Create additional users
db.createUser({
  user: "ian",
  pwd: "point90",
  roles: [{ role: "readWrite", db: "ThinkDB" },
           { role: "readAnyDatabase", db: "admin" }]
});
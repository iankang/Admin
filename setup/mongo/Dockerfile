# Use the official MongoDB community image as the base image
FROM mongo:4.2
# Set the environment variables
ENV MONGO_INITDB_ROOT_USERNAME admin
ENV MONGO_INITDB_ROOT_PASSWORD password

# Copy the script to create users and database
COPY create-users.js /docker-entrypoint-initdb.d/
COPY create-database.js /docker-entrypoint-initdb.d/

# Grant executable permissions to the scripts
RUN chmod +x /docker-entrypoint-initdb.d/create-users.js
RUN chmod +x /docker-entrypoint-initdb.d/create-database.js

VOLUME /data/db
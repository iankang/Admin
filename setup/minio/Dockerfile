# Use the official MinIO image as the base image
FROM minio/minio

# Set the environment variables
ENV MINIO_ROOT_USER=think-access-key
ENV MINIO_ROOT_PASSWORD=think-secret-key
ENV MINIO_API_PORT_NUMBER=9000
ENV MINIO_CONSOLE_PORT_NUMBER=9001
# Expose the MinIO web GUI port
EXPOSE 9000
EXPOSE 9001

# Set the data directory for storage
ENV MINIO_DATA_DIR=/data

# Set the configuration directory
ENV MINIO_CONFIG_DIR=/root/.minio

# Create the data and configuration directories
RUN mkdir -p $MINIO_DATA_DIR $MINIO_CONFIG_DIR

# Set the entrypoint command to start MinIO server
ENTRYPOINT ["minio", "server", "--console-address", ":9001", "/data"]

# Set the volume to be mounted
VOLUME /data
### How to Generate auth key for ssl

```
# Create keystore
echo "Refreshing '~/home/ikangethe/key"
sudo openssl pkcs12 -export   -in /etc/letsencrypt/live/auth.think.ke/cert.pem   -inkey /etc/letsencrypt/live/auth.think.ke/privkey.pem   -out auth.think.ke.keystore.p12   -name auth.think.ke   -CAfile /etc/letsencrypt/live/auth.think.ke/chain.pem   -caname auth.think.ke   -password pass:kangethe

echo "Refreshing '~/home/ikangethe/key"

echo "Removing the keystore"
rm /home/ubuntu/key/auth.think.ke.keystore

keytool -importkeystore \
        -deststorepass kangethe \
        -destkeypass kangethe \
        -deststoretype pkcs12 \
        -srckeystore /home/ubuntu/key/auth.think.ke.p12 \
        -srcstoretype PKCS12 \
        -srcstorepass kangethe \
        -destkeystore /home/ubuntu/key/auth.think.ke.keystore \
        -alias auth.think.ke
```

### How to create the fusion auth application

```
version: '3'

services:
  fusionauth:
    image: fusionauth/fusionauth-app:latest
    environment:
      DATABASE_URL: jdbc:postgresql://18.218.12.248:37821/fusionauth
      FUSIONAUTH_APP_RUNTIME_MODE: development
     # FUSIONAUTH_APP_URL: http://fusionauth:9011
      DATABASE_ROOT_USERNAME: fusionauth
      DATABASE_ROOT_PASSWORD: fusionauth
      DATABASE_USERNAME: fusionauth
      DATABASE_PASSWORD: fusionauth

    restart: unless-stopped
    ports:
      - 9011:9011
      - 9013:9013
    volumes:
      - /home/ubuntu/fusionauth/fusionauth.properties:/usr/local/fusionauth/config/fusionauth.properties
      - /home/ubuntu/fusionauth/chain.pem:/usr/local/fusionauth/config/chain.pem
      - /home/ubuntu/fusionauth/cert.pem:/usr/local/fusionauth/config/cert.pem
      - /home/ubuntu/fusionauth/fullchain.pem:/usr/local/fusionauth/config/fullchain.pem
      - /home/ubuntu/fusionauth/privkey.pem:/usr/local/fusionauth/config/privkey.pem
      - /home/ubuntu/fusionauth/auth.think.ke.p12:/usr/local/fusionauth/config/auth.think.ke.p12
      - /home/ubuntu/fusionauth/auth.think.ke.keystore:/usr/local/fusionauth/config/auth.think.ke.keystore
```

### fusion auth config.properties
```

database.url=jdbc:postgresql://18.218.12.248:37821/fusionauth
database.username=fusionauth
#proxy.host="https://auth.think.ke/fusion"
#proxy.port=443
database.password=fusionauth
fusionauth-app.url=http://18.218.12.248
fusionauth-app.http.port=9013
fusionauth-app.https.port=9011
fusionauth-app.https.enabled=true
fusionauth-app.https.certificate-file=/usr/local/fusionauth/config/fullchain.pem
fusionauth-app.https.private-key-file=/usr/local/fusionauth/config/privkey.pem
fusionauth-app.local-metrics.enabled=true
fusionauth-app.memory=1G
fusionauth-app.search-engine-type=database
#fusionauth-app.additional-java-args=-Djavax.net.ssl.keyStore=/usr/local/fusionauth/config/cacerts -Djavax.net.ssl.keyStorePassword=kangethe -Djavax.net.ssl.trustStore=/usr/local/fusionauth/config/cacerts -Djavax.net.ssl.trustStorePassword=kangethe

```

### Running docker compose
```
    docker-compose pull
    docker-compose up --force-recreate --build -d
    docker image prune -f

```
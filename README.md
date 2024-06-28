# How to Generate auth key for ssl

```
# Create keystore
echo "Refreshing '~/home/ikangethe/key"
openssl pkcs12 -export \
         -in /etc/letsencrypt/live/auth.think.ke/cert.pem \
         -inkey /etc/letsencrypt/live/auth.think.ke/privkey.pem \
         -out /home/ubuntu/key/auth.think.ke.p12 \
         -name auth.think.ke \
         -CAfile /etc/letsencrypt/live/auth.think.ke/fullchain.pem \
         -caname "Let's Encrypt Authority X3" \
         -password kangethe

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
# How to Generate auth key for ssl

```
openssl pkcs12 -export \
  -in /etc/letsencrypt/live/auth.think.ke/cert.pem \
  -inkey /etc/letsencrypt/live/auth.think.ke/privkey.pem \
  -out /etc/letsencrypt/live/auth.think.ke/auth.think.ke.keystore \
  -name auth.think.ke \
  -CAfile /etc/letsencrypt/live/auth.think.ke/chain.pem \
  -caname auth.think.ke \
  -password pass:kangethe
```
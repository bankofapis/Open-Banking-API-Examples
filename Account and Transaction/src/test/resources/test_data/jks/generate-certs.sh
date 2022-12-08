#!/bin/bash

# Create server self-signed cert and key pair
openssl req -x509 -newkey rsa:2048 -utf8 -days 3650 -nodes -config ca-cert.conf -keyout ca-cert.key -out ca-cert.crt

# Create server PKCS12 keystore
openssl pkcs12 -export -inkey ca-cert.key -in ca-cert.crt -out ca-cert.p12 -name CARoot -password "pass:password"

# Convert server PKCS12 keystore to JKS
keytool -importkeystore -deststorepass "password" -destkeypass "password" -srckeystore ca-cert.p12 -srcstorepass "password" -deststoretype jks -destkeystore ca-cert.jks

# Create client keystore
keytool -genkey -storepass password -alias testClient -keyalg RSA -keystore client-cert.jks -keysize 2048 -dname "C=GB,ST=London,O=WireMock,emailAddress=tom@wiremock.org,CN=localhost"

# Generate new cert-signing request
keytool -keystore client-cert.jks -storepass "password" -certreq -alias testClient -keyalg rsa -file client.csr

# Sign client cert from request
openssl  x509  -req  -CA ca-cert.crt -CAkey ca-cert.key -in client.csr -out client.cer -days 3650 -CAcreateserial

# Build cert chain
cat ca-cert.crt >> client.cer

# Import signed cert to client keystore
keytool -trustcacerts -storepass "password" -keystore client-cert.jks -alias testClient -import -file client.cer

# import client cert to server truststore
keytool  -trustcacerts -storepass "password" -keystore ca-cert.jks -alias testClient -import -file client.cer

# Delete all files that aren't our server and client JKS files
rm ca-cert.key ca-cert.p12 client-cert.crt client-cert.key client-cert.p12 client-cert.pkcs12 ca-cert.crt client.csr client.cer ca-cert.srl 2>/dev/null
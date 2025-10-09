#!/bin/sh

keytool -genkeypair -v \
    -keystore keystore.jks \
    -storetype JKS \
    -alias gym-meet \
    -keyalg RSA -keysize 2048 \
    -validity 10000

mv keystore.jks ..
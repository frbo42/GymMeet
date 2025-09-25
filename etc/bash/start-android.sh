#!/bin/sh

docker run -d \
  -p 6080:6080 \
  -p 5555:5555 \
  -e EMULATOR_DEVICE="Samsung Galaxy S10" \
  -e WEB_VNC=true \
  -e ADB_SERVER=true \
  --device /dev/kvm \
  --name android-simulator \
  budtmo/docker-android:emulator_14.0


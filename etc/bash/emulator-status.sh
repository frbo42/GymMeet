#!/bin/sh


docker exec -it android-simulator cat device_status

#connect to simulator
adb connect localhost:5555
# Perfect Popcorn Popper
Native Android application code for Fall 2021 senior design project. This project encompassed the creation of custom hardware that would be placed on the door of a microwave and listen for pops in the microwave. Based on our algorithms the device would calculate the amount of time remaining on a standard-sized bag of popcorn. Upon detection of certain events such as 30 seconds remaining or an optimal doneness, the hardware (connected over Bluetooth to an Android device) would send a BLE packet which would prompt the mobile application to provide a notification to the user. 

## Description
This application interfaces over a custom-built implementation of BLE to an Adafruit BLE chip that was implemented in the hardware portion of the project. After connecting to the device, the application will listen for broadcasts from the device that will signal it to provide the user with a variety of notifications regarding the status of their popcorn. 

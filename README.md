# PROBLEM STATEMENT 
The proposed Accident Safety app aims to be a comprehensive and immediate solution to most traffic accidents. It uses in-built phone sensors to determine the occurrence of any accidents in the background, and provides an SOS alert to every nearby user. Then, it provides any first responders with an AR-based guide to treat any injuries detected via scan, with interactive voice-based directions. In brief, it utilizes various cutting-edge technologies to handle any accidents efficiently and in every possible way and reduce all casualties and damages.
# APPROACH 
 ## AUTOMATIC ACIDENT DETECTION
The app leverages smartphone sensors like accelerometer and GPS, in background, to detect sudden changes in motion or impact, potentially signaling a vehicle crash.
 ## Real-Time AR-based First Aid Guide
 The app provides an AR-integrated guide to provide first-aid treatment to any quick responders. The user can scan the injured person's body, and the app uses an AR overlay to highlight all grave injuries and their potential severity. It also provides voice instructions to treat them and a chatbot to ask directions.
 ## Emergency Services
 The App automatically sends an SOS signal to nearby emergency services like a hospital and police station. The app also utilizes Mapbox to always show the quickest paths to and fro any emergency service
 ## Nearby Users
 The app sends an alert notification to all users of the app within a 2-km radius of the accident of it’s occurrenceand also provides them with the quickest path on an interactible map. It also suggests them with items that might prove helpful on the scene based on camera scans from the accident victims phone camera.  
# TECH STACK 
### ->Firebase
### ->TFLite
### ->Kotlin & XML
### ->Mapbox
### ->Google Media Pipe
### ->Android Sensors 
# Basic Workflow
## I. AUTOMATIC ACCIDENT DETECTION
 The algorithm uses accelerometer, GPS, and activity recognition API to detect car collisions.
 It waits for high acceleration above 4g(g=10m/s2).
 It then looks for a new GPS sample with similar speed if same location is  retrieved then Accident can occur
 The algorithm sends notifications to users within a 2 km radius and provides a route to the site.

## II. AR-BASED INJURY DETECTION AND FIRST AID GUIDE
The first Responders are provided with an AR-guide for quick first AID administration.
The person can scan the injured person’s body to  detect any injuries that can be treated.
The video feed is passed to a body-landmark detection model which detects all the major landmarks on a human body. Based on this we crop the frames up into it’s various body parts and pass these individual cropped images into an image detection model. This returns any major injuries that can be found. 
The app then provides the information back to the user with an AR overlay with possible quick treatments and gives voice directions.
The responders are also provided a chatbot based interaction to ask for doubts regarding administration of first-aid.

## 

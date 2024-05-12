![flaticon_accident](/front.png)
### A project made with ❤️ by 
* [Aritra Biswas](https://github.com/AritraBiswas9788)
* [Aditya Kumar](https://github.com/ak79036)
* [Abhinav Jain](https://github.com/jainjabhi05)
# PROBLEM STATEMENT 
The proposed Accident Safety app aims to be a comprehensive and immediate solution to most traffic accidents. It uses in-built phone sensors to determine the occurrence of any accidents in the background, and provides an SOS alert to every nearby user. Then, it provides any first responders with an AR-based guide to treat any injuries detected via scan, with interactive voice-based directions.The app users in the proximity are notified so that they can take neccesary actions and also the vehicles approaching near the accident area are informed of the danger zone . In brief, it utilizes various cutting-edge technologies to handle any accidents efficiently and in every possible way and reduce all casualties and damages.
# Why we chose this idea ?
Each year, around 1.19 million people lose their lives due to road traffic crashes. Immediate and proper first aid, blood supplies, and hospital equipment availability can significantly improve survival chances in critical moments. However, bystanders often lack the knowledge and confidence to intervene effectively, leading to delays in receiving medical attention that can worsen the outcome. Developing cutting-edge technologies would empower bystanders with the knowledge, skills, and tools they need to save lives and minimize the impact of these tragic incidents.
# APPROACH 
 ## 1.Automatic Accident Detection 🚘
The app leverages smartphone sensors like accelerometer and GPS, in background, to detect sudden changes in motion or impact, potentially signaling a vehicle crash.
 ## 2.Real-Time AR-based First Aid Guide ⚕️
 The app provides an AR-integrated guide to provide first-aid treatment to any quick responders. The user can scan the injured person's body, and the app uses an AR overlay to highlight all grave injuries and their potential severity. It also provides voice instructions to treat them and a chatbot to ask directions.
 ## 3.Emergency Services 🏥
 The App automatically sends an SOS signal to nearby emergency services like a hospital and police station. The app also utilizes Mapbox to always show the quickest paths to and fro any emergency service
 ## 4.Nearby Users 🧍
 The app sends an alert notification to all users of the app within a 2-km radius of the accident of it’s occurrenceand also provides them with the quickest path on an interactible map. It also suggests them with items that might prove helpful on the scene based on camera scans from the accident victims phone camera.  
# TECH STACK 
### ->Firebase
### ->TFLite
### ->Kotlin & XML
### ->Mapbox
### ->Google Media Pipe:
### ->Android Sensors 
### ->GeoFire
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
# Market Adaptability

* Universal Need: Road accidents are a global problem, making the app relevant in most countries.
* Smartphone Penetration: With smartphone usage reaching high levels worldwide, the app can reach a large user base without requiring additional hardware.
* Focus on Bystanders: The app empowers everyday people, not just medical professionals, to make a difference in emergencies.
* Scalability: The app can be easily adapted to different regions by integrating with local emergency services, blood bank networks, and hospital databases.



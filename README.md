# ELCare

By Team `LastPlace`: comprised of
1. [Loh Kar Wei: Asienwald](https://github.com/Asienwald): Development of Android App functionality, app design and Watson API Integration
2. [Tan Jia Wei: Hayashion](https://github.com/Hayashion): Designed optimal course of action for emergencies, Video Demonstration and Watson API Integration
3. [Chan Zun Mun Terence: Hackin7](https://github.com/Hackin7): IoT functionality, setting up of central IoT server and web layout


## Contents

1. [Short description](#short-description)
1. [Demo video](#demo-video)
1. [The architecture](#the-architecture)
1. [Long description](#long-description)
1. [Getting started](#getting-started)
1. [Built with](#built-with)
1. [Images](#images)

## Short description

### What's the problem?

#### Emergency Medical Services
SCDF works closely with Community First Responders (CFRs) to provide timely relief and response to emergency situations. 


With the increasingly aging population in Singapore, not everyone of them is able to receive effective early emergency response due to inadequate emergency detection.

### How can technology help?

With the recent advances in Artificial Intelligence, Natural Language Processing, Speech Recognition, we can create interfaces to allow people to get timely medical help easily

### The idea

We have designed the app ELCare to help the elderly in emergencies. Through it's various functionalities, such as Speech to Text, it can help identify when an elderly is in danger, and get the necessary help before it is too late.

## Demo video

![Watch the video](https://youtu.be/M_g4lS14vlk)

## The architecture

### System design
![](https://i.imgur.com/U4DqkNx.png)
1. The Android app is coded in Java with Android Studio
2. It connects to a server (Through REST APIs) to retrieve data from IoT devices
    * The server is coded in Flask (Python) and stores the current information about the data
    * The IoT Devices can connect to the server and send the current data (similarily through REST APIs)
3. The app also connects to IBM's servers to use the Speech to Text, Tone Analyzer and Watson Assistant APIs

### Usage
![](https://i.imgur.com/VWi6bW7.png)

1. The elderly would open the application
2. Open `Chat` or `Speak` and communicate with Jolene
3. Jolene will be able to identify the help needed
4. For related individuals, they can access the `Monitor` section on the app, or use the website to monitor the elderly's situation
    a. They can also choose to receive notifications through email in the event of any emergency situations. First responders would also be notified through such communication platforms.

### Long description

[More detail is available here](DESCRIPTION.md)

## Getting started
### Setting up the Application
You can download the app [here](). Alternatively, you can compile it in Android studio, but take note to set the appropriate API
### Setting up the Server
To set up the server (acting as a bridge between the IoT device and the app)
1. Copy `API_KEYS_sample.py` to API_KEYS.py and fill in your API Keys and URLs for SendGrid, IBM STT and IBM Tone Analyzers
2.  Build the Dockerimage by running `docker build Web\ Server` and `
docker run -p 5000:5000 <container-id>`
    * Alternatively, you can use the currently hosted server at [https://scdf-x-ibm-web.herokuapp.com/](https://scdf-x-ibm-web.herokuapp.com/) (This url is hardcoded in many aspects of this system).
    * If you set up a server, make sure you can connect to it through https
3. If you are checking the dashboard, or using the device monitoring feature, login with the default credentials
    * Username: `GodMode`
    * Password: `yeshucpo`

### Setting up sample IOT Device
1. Use an ESP8266. 
2. Connect a sound sensor to GPIO 5, and a PIR sensor to GPIO 4. 
8. Provide power to all components
9. Fill in the Wifi AP Name and Password and replace the server url with your own. Upload the sketch in [`IOT Hardware/SimpleMonitoring/`](https://github.com/Asienwald/SCDF-x-IBM-Lifesavers-Innovation/tree/master/IOT%20Hardware/SimpleMonitoring)
10. To use the temperature sensor, connect a DHT22 sensor to GPIO 0, and uncomment the appropriate  lines of code


## Built with
### IBM Technologies Used
1. [Watson Speech To Text](https://www.ibm.com/sg-en/cloud/watson-speech-to-text)
2. [Watson Tone Analyzer](https://www.ibm.com/watson/services/tone-analyzer/)
3. [Watson Assistant](https://www.ibm.com/cloud/watson-assistant/)

### Other Technologies Used
- Android Studio
- Invision Studio
- Docker and Heroku (for Deployment of the Web server)
- Google's Teachable Machine 2.0 (for pose detection for a falling person)
    - Includes Tensorflow.js and PoseNet
- ESP8266(NodeMCU) and Arduino Programming Language

## Possible Improvements
1. We could interface with the actual first responder API to better notify first responders to help the elderly
2. We could have implemented a database to store information more effectively

## Images
### App Images

#### Home Page
![](https://i.imgur.com/GO3S5wL.png)

#### Speak Page
![](https://i.imgur.com/Qr1QC3G.png)

#### Contacts Page
![](https://i.imgur.com/pCC00l3.png)

#### Monitoring Page
![](https://i.imgur.com/nTSOkYH.png)

#### SOS Page
![](https://i.imgur.com/Zyjy0ZY.png)

#### Chat Page
if it's not convenient to speak
![](https://i.imgur.com/RmgFdST.png)

### Sample IOT Device
![](https://i.imgur.com/8VpKMFP.png)

### Web Server

#### Login
![](https://i.imgur.com/vUb3woJ.png)

#### Dashboard
![](https://i.imgur.com/bHXEmgg.png)

#### Set your personal computer as a monitoring device
![](https://i.imgur.com/63wldKc.png)

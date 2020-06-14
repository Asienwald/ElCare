# Description of ELCare

The heart of our app is the virtual assistant Jolene, powered by IBM watson assistant. There are various means for the user to interact with her to get help.
We have the chat feature, which the user can type in their request in, and a voice feature for the user to speak to Jolene.

Jolene will then use the IBM tone analyzer to analyze the user's tone and identify keywords to see if the user needs help.
The app also monitors falls using the phone's accelerometer and will automatically trigger listening mode to listen out for cries for help.

Our app also can integrate with Internet of Things (IoT) devices to monitor motion, sound and temperature around the house. This allows it to determine abnormal activity patterns and find out if the user needs help.

Once an emergency is identified, the app identifies the user's location and sends out an alert to their emergency contact and the community first responders via the myResponder app.

There is also a Web Based User Interface which can be used by family to monitor the current conditions of the elderly at home. It can also inform any other relavant parties (eg. family) about such emergencies (through SendGrid Email Service).

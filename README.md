SMSRelay
========

This app is a demo-app which allows you to receive and send text messages from the browser
It is constituted of two parts:
- an android app
- a server part


What is used
------------
The android app was developped using plain java. For the server, I used the Play Framework (2.2) and angularjs (+ Yo /gruntJs) for the front-end part. 

How does it work
----------------
A broadcast receiver is registered in the Android application, so the app is notified when you receive a SMS. Then we read the SMS and send the content and basic information about the sender to our server. The server will receive this message and relay it to the browser using SSE where it will be displayed. 

When you reply in the browser, the message is posted to the server. The server will then make a call to the Google servers (Google Cloud Messaging or GCM). Google will then deliver the message to your phone, and here an other broadcast receiver will handle the message and send the SMS.

Notes
-----
This application was developped rather quickly, no security is guaranteed. The purpose was to have fun :)

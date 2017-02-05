# LiftTracker
Vision Tracking for the 2017 FRC Game

[![Build Status](https://travis-ci.org/TheGuyWhoCodes/LiftTracker.svg?branch=master)](https://travis-ci.org/TheGuyWhoCodes/LiftTracker)

Here are the steps to get this working:
 - Clone the repository, using `git clone https://github.com/TheGuyWhoCodes/LiftTracker.git`
 - Make sure you have the opencv library downloaded, and Network Tables 3.0 (included)
 - Import the project into Eclipse, and go to your build path and add the included NetworkTables jar, and opencv-XXX.jar that you downloaded
 - Change the variables inside the code to your situation, and calculate the distance constant (tutorial in my post [here](https://www.chiefdelphi.com/forums/showthread.php?p=1638376#post1638376))
 - Open the .grip file included inside of GRIP. Tune your HSV values to your liking using your webcam and go to Tools->Generate code
 - Using the Generate Code feature will export a *.java file. Open that file and copy all the code, and replace the LiftTracker.java code inside the project
 - After that, export it as a runnable jar, (File->Export->Java->Runnable JAR file). 
 - Run the jar file using `java -jar blahblahblah.jar`
 - Vision Track!
 
If you have any issues, open up an issue and I'll be happy to look at it. 

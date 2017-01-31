# LiftTracker
Vision Tracking for the 2017 FRC Game


S.W.A.T 1806 is proud to announce the first Lift Tracking software of the 2017 game FIRST Steamworks. No longer will mentors and coaches have to yell at the programmers to get vision tracking working, it's here. The software will recognize the distance from the target, and the angle to the target. This will also run on different processing computers like the PI and Kangaroo with relative ease. You can also easily edit this software by using the included GRIP file and generate the code that you like, no more messing with pesky HSV!

How to install:
Install opencv 3.X on whatever computer
Download NetworkTables 3.0 (inside repo) and make sure it's in the build path
Download the repo
Run GRIP with the included project file, and tune your values to your liking, and export the code and overwrite everything in LiftTracker.java BE SURE TO NOT DELETE LINES 303-310, KEEP IT SOMEWHERE IN THAT CODE
Export the project as a runnable jar
Run it using command line or a batch file (or .sh file if you are on linux)
Before you run it though, you need to calculate the distance constant. This is a pretty easy task and should take under 10 min. Choose 5 distances for the robot to sit (12, 24, 48, 60, 72 in). Move your robot to each of these distances and record the variable lengthBetweenContours, then write that down. Multiply the distance and lengthBetweenContours and write what you get down. After you do that for all of the values, average everything and that's the distance constant. There is a variable in the code that you can change named DISTANCE_CONSTANT so you can easily change it


If you have any questions, feel free to post a comment or make an issue on the GitHub page and I'll be happy to look at it. Currently this is in beta, so please contact me for corrections.

Thanks to:
Fauge7, you a G
TowerTracker 1.0 for giving us some inspiration on what to do
S/O to the people on the FRC Discord for overcoming problems we faced

FAQ:


Q:What camera did you guys use

A: Microsoft Lifecam HD3000 @ 640 x 480

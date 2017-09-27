import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
public class Processing {

	static{ 
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load("/home/ubuntu/Desktop/lib/libntcore.so");
		System.load("/home/ubuntu/Desktop/lib/libopencv_java330.so");

	}
//	Process for GRIP	
	static LiftTracker tracker;
	public static VideoCapture videoCapture;
//	Constants for known variables
	static Mat matOriginal;
	static Mat matFlipper;
	public static final double OFFSET_TO_FRONT = 0;
	public static final double CAMERA_WIDTH = 320;
	public static final double DISTANCE_CONSTANT= 5738;
	public static final double WIDTH_BETWEEN_TARGET = 8.5;
	public static final double ANGLE_OFFSET =  0;
	public static boolean shouldRun = true;
	static NetworkTable table;
	
	
	static double lengthBetweenContours;
	static double distanceFromTarget;
	static double lengthError;
	static double[] centerX;
	static double HEIGHT_CLOSENESS = .15;
	
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setTeam(1806);
		NetworkTable.setIPAddress("10.18.6.2"); //TODO FIX THIS LINK AT COMP
		NetworkTable.initialize();
		table = NetworkTable.getTable("LiftTracker");
		
		while(shouldRun){
			try {
//				opens up the camera stream and tries to load it
				videoCapture = new VideoCapture();
				tracker = new LiftTracker();
				videoCapture.open(0);  //TODO FIX THIS LINK AT COMP
				videoCapture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 320);
				videoCapture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 240);
				// change that to your team number boi("http://roborio-XXXX-frc.local:1181/?action=stream");
				while(!videoCapture.isOpened()){
					System.out.println("Didn't open Camera, restart jar");
				}
//				time to actually process the acquired images
				while(videoCapture.isOpened()){
					processImage();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
//		make sure the java process quits when the loop finishes
		videoCapture.release();
		System.exit(0);
	}
	public static void processImage(){
		System.out.println("Processing Started");
		 matOriginal = new Mat();

//		only run for the specified time
		while(true){
			matFlipper = new Mat();
			//System.out.println("Hey I'm Processing Something!");
			videoCapture.read(matOriginal);
			tracker.process(matOriginal);
			returnCenterX();
			System.out.println(getAngle());
			table.putDouble("distanceFromTarget", distanceFromTarget());
			table.putDouble("angleFromGoal", getAngle());
			table.putNumber("numberOfContours", tracker.filterContoursOutput().size());
			//table.putNumberArray("centerX", centerX);
		}
		
	}
	public static double returnCenterX(){
		double[] defaultValue = new double[0];
			// This is the center value returned by GRIP thank WPI
			if(!tracker.filterContoursOutput.isEmpty() && tracker.filterContoursOutput.size() >= 2 && tracker.filterContoursOutput().size() <=8){
				Rect r = Imgproc.boundingRect(tracker.filterContoursOutput.get(1));
				Rect r1 = Imgproc.boundingRect(tracker.filterContoursOutput.get(0)); 
				centerX = new double[]{r1.x + (r1.width / 2), r.x + (r.width / 2)};
				//System.out.println(centerX.length); //testing
				// this again checks for the 2 shapes on the target
				if(tracker.filterContoursOutput.size() == 2){
					// subtracts one another to get length in pixels
					lengthBetweenContours = Math.abs(centerX[0] - centerX[1]);
					System.out.println("I see: " + centerX.length);
				} else {
					Rect[] rectangleArray = new Rect[tracker.filterContoursOutput.size()];
					System.out.println("I see: " + rectangleArray.length);
					for(int i = 0 ; i < tracker.filterContoursOutput.size(); i++){
						rectangleArray[i] = Imgproc.boundingRect(tracker.filterContoursOutput().get(i)); 
						System.out.println("Object" + i + " X " + rectangleArray[i].x + " Y "+rectangleArray[i].y + "Width = " + rectangleArray[i].width);
					}
					ArrayList<ArrayList<Integer>> Pairs = new ArrayList<ArrayList<Integer>>();
					for(int i = 0; i < rectangleArray.length; i++){
						for(int j =i+1; j < rectangleArray.length; j++){
							if(rectangleArray[i].height * (1-HEIGHT_CLOSENESS) <= rectangleArray[j].height 
									&& rectangleArray[i].height * (1+HEIGHT_CLOSENESS) >= rectangleArray[j].height){
									ArrayList<Integer> tempPairs = new ArrayList<Integer>();
									tempPairs.add(i);
									tempPairs.add(j);
									Pairs.add(tempPairs);
									System.out.println("\t Found Pair" + i + "and " + j);
							}
							
						}
					}
					if(Pairs.size() != 0){
						double bestDistance = 1000000;
						int currentBest = -1;
						for(int i = 0; i < Pairs.size(); i++){
							ArrayList<Integer> tempPairs = Pairs.get(i);
							 r = rectangleArray[tempPairs.get(0)];
							 r1 = rectangleArray[tempPairs.get(1)];
							double[] r1Points = {r.x + (r.width /2) , r.y + (r.height / 2)};
							double[] r2Points = {r1.x + (r1.width /2) , r1.y + (r1.height / 2)};
							double distanceBetweenPoints = Math.sqrt(Math.pow((r2Points[0] - r1Points[0]), 2) + (Math.pow((r2Points[1] - r1Points[1]), 2)));
							System.out.println("\t r1 X : " + r.x);
							System.out.println("\t r2 X : " + r1.x);

							if(distanceBetweenPoints < bestDistance){
								//System.out.println("\t Best Distance = " + distanceBetweenPoints );
								currentBest = i;
								bestDistance = distanceBetweenPoints;
							} 
						}
						ArrayList<Integer> tempPairs = Pairs.get(currentBest);
						r = rectangleArray[tempPairs.get(0)];
						r1 = rectangleArray[tempPairs.get(1)];	
						centerX = new double[]{r.x + (r.width / 2), r1.x + (r1.width / 2)};
						System.out.println("\t Best Pairs Found: " + tempPairs.get(0) +" "+ tempPairs.get(1));
						// subtracts one another to get length in pixels
						//lengthBetweenContours = Math.abs((centerX[0] + centerX[1]) / 2) - 320;
						lengthBetweenContours = Math.abs((centerX[0] + centerX[1]) / 2) - CAMERA_WIDTH /2;
					}
				}
			}
		return lengthBetweenContours;
	}
	
	public static double distanceFromTarget(){
		// distance costant divided by length between centers of contours
		distanceFromTarget = DISTANCE_CONSTANT / lengthBetweenContours;
		return distanceFromTarget - OFFSET_TO_FRONT; 
	}
	public static double getAngle(){
		// 8.5in is for the distance from center to center from goal, then divide by lengthBetweenCenters in pixels to get proportion
		double constant = WIDTH_BETWEEN_TARGET / lengthBetweenContours;
		double angleToGoal = 0;
			//Looking for the 2 blocks to actually start trig
		if(!tracker.filterContoursOutput.isEmpty() && tracker.filterContoursOutput.size() >= 2){

			if(centerX.length == 2){
				// this calculates the distance from the center of goal to center of webcam 
				double distanceFromCenterPixels= ((centerX[0] + centerX[1]) / 2) - (CAMERA_WIDTH / 2);
				// Converts pixels to inches using the constant from above.
				double distanceFromCenterInch = distanceFromCenterPixels * constant;
				// math brought to you buy Chris and Jones
					angleToGoal = Math.atan(distanceFromCenterInch / distanceFromTarget());
					angleToGoal = Math.toDegrees(angleToGoal);
					angleToGoal = -angleToGoal - ANGLE_OFFSET;
					Imgcodecs.imwrite("output.png", matOriginal);
					
					}
			}
			return angleToGoal;
	}

}
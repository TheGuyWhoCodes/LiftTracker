package org.usfirst.frc.team5496.robot.vision;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
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

import edu.wpi.first.wpilibj.networktables.NetworkTable;
public class Processing {

	static{ 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	}
	
//	Process for GRIP	
	private LiftTracker tracker;
	private  static VideoCapture videoCapture;
//	Constants for known variables
	private Mat matOriginal;
	public static final double OFFSET_TO_FRONT = 0;
	public static final double CAMERA_WIDTH = 640;
	public static final double DISTANCE_CONSTANT= 5738;
	public static final double WIDTH_BETWEEN_TARGET = 8.5;
	public static boolean shouldRun = true;
	private NetworkTable table;
	
	
	private double lengthBetweenContours;
	private double distanceFromTarget;
	private double lengthError;
	private double[] centerX;
	
	private  Integer teamNumber = 5496;
	private  String networkTableName = "LiftTracker";
	private final Properties props;
	
	private String getRoboRioHostName() {
		return "roborio-" + teamNumber + "-frc.local";
	}
    
	private void setupNetworkTables()  {
    	NetworkTable.setClientMode();
		NetworkTable.setTeam( teamNumber );
		NetworkTable.setIPAddress( getRoboRioHostName() );
		NetworkTable.initialize();
		table = NetworkTable.getTable(networkTableName);
			
    }
	
    public void init() {
    	setupNetworkTables();
    }
    
    private void processProps() {
    	teamNumber = Integer.parseInt(props.getProperty("team.number"));
    	networkTableName = props.getProperty("networktablename", "LiftTracker");	
    }
    public Processing(Properties props) {
    	super();
    	this.props = props;
    	processProps();
    }
	private double distanceFromTarget(){
		// distance costant divided by length between centers of contours
		distanceFromTarget = DISTANCE_CONSTANT / lengthBetweenContours;
		return distanceFromTarget - OFFSET_TO_FRONT; 
	}
	
	private double getAngle(){
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
				// prints angle
				//System.out.println("Angle: " + angleToGoal);
				}
			}
			return angleToGoal;
	}
	
	public  void processImage(){
		 System.out.println("Processing Started");
		 matOriginal = new Mat();

//		only run for the specified time
		while(true){
			//System.out.println("Hey I'm Processing Something!");
			videoCapture.read(matOriginal);
			tracker.process(matOriginal);
			returnCenterX();
			System.out.println(getAngle());
			table.putDouble("distanceFromTarget", distanceFromTarget());
			table.putDouble("angleFromGoal", getAngle());
			table.putNumberArray("centerX", centerX);
			videoCapture.read(matOriginal);
		}
		
	}
	
	public double returnCenterX(){
		double[] defaultValue = new double[0];
			// This is the center value returned by GRIP thank WPI
			if(!tracker.filterContoursOutput.isEmpty() && tracker.filterContoursOutput.size() >= 2){
				Rect r = Imgproc.boundingRect(tracker.filterContoursOutput.get(1));
				Rect r1 = Imgproc.boundingRect(tracker.filterContoursOutput.get(0)); 
				centerX = new double[]{r1.x + (r1.width / 2), r.x + (r.width / 2)};
				Imgcodecs.imwrite("output.png", matOriginal);
				//System.out.println(centerX.length); //testing
				// this again checks for the 2 shapes on the target
				if(centerX.length == 2){
					// subtracts one another to get length in pixels
					lengthBetweenContours = Math.abs(centerX[0] - centerX[1]);
				}
			}
		return lengthBetweenContours;
	}
	
	public void process() {
		while(shouldRun){
			try {
                //opens up the camera stream and tries to load it
				videoCapture = new VideoCapture();
				tracker = new LiftTracker();
				videoCapture.open("http://roborio-1806-frc.local:1181/?action=stream");
				// change that to your team number boi("http://roborio-XXXX-frc.local:1181/?action=stream");
				while(!videoCapture.isOpened()){
					System.out.println("Didn't open Camera, restart jar");
				}
                //time to actually process the acquired images
				while(videoCapture.isOpened()){
					processImage();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		FileInputStream reader;
		Properties props = new Properties();
		
		try {
			reader = new FileInputStream(new File("vision.properties"));
			props.load(reader);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} 
		
		Processing processing = new Processing( props );
		processing.setupNetworkTables();
		processing.process();
		
		videoCapture.release();
		System.exit(0);
	}



}

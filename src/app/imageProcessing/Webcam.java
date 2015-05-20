package app.imageProcessing;

import java.util.List;

import app.Parent;
import app.controllers.MainController;
import app.imageProcessing.solver.DetectionSolver;
import app.imageProcessing.solver.ImageProcessingSolver;
import app.imageProcessing.solver.TwoDThreeD;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

public class Webcam extends Parent{

	private static final String BOARD_IMAGE= "1.png"; 
	private final static boolean INTERACTION_MODE = false;
	public final static int PROCESS_EACH_X_FRAME = 30;
	
	public static float scale = 0.7f;

	private PImage currentImg;
	private ImageProcessingSolver processing;
	private DetectionSolver detection;
	private TwoDThreeD converter;

	private PImage imageProcessed;
	private List<PVector> intersections; 
	private PVector rotationAngles;
	
	private Capture cam;
	private PGraphics resultImage;

	private boolean doStepOneOnlyOnce = false;
	
	public Webcam(PApplet parent) {
		super(parent);
		
		currentImg = p.loadImage(BOARD_IMAGE);
		
		if (INTERACTION_MODE) {
			String[] cameras = Capture.list(); 
			cam = new Capture(parent, cameras[3]); 
			cam.start();
		} 
		rotationAngles = new PVector();
		resultImage = p.createGraphics((int)(MainController.WINDOW_WIDTH * scale), (int)(MainController.WINDOW_HEIGHT * scale),PApplet.P2D);


	}
	
	public void update() {
		
		if (INTERACTION_MODE) {
			if (cam.available()) {
				cam.read();
			}
			currentImg = cam.get();
		} 
		
		if (p.frameCount % PROCESS_EACH_X_FRAME == 0) { 
			
			long globalStartTime = System.currentTimeMillis();
			
			//initialize the solvers
			processing = new ImageProcessingSolver(p);

			detection = new DetectionSolver(p, resultImage);
			converter = new TwoDThreeD((int)(MainController.WINDOW_WIDTH*scale),(int)(MainController.WINDOW_HEIGHT*scale));
				
			// 1st step
			long startTime = System.currentTimeMillis();
			//if (!doStepOneOnlyOnce) {
				//doStepOneOnlyOnce = true;
				imageProcessed =  processing.solve(currentImg);
			//}

			long estimatedTime = System.currentTimeMillis() - startTime;
			if (estimatedTime > 200) System.err.println("\n FIRST STEP [MS] = " + estimatedTime);
			
			// 2nd step
			//Get the (x,y) coordinates of the corners after the detection algorithms
			startTime = System.currentTimeMillis();
			intersections = detection.solve(imageProcessed);
			estimatedTime = System.currentTimeMillis() - startTime;
			if (estimatedTime > 200) System.err.println("\n SECOND STEP [MS] = " + estimatedTime);
			
			// 3th step
			if (intersections!= null && intersections.size() >= 4) {
				startTime = System.currentTimeMillis();
				rotationAngles = converter.get3DRotations(intersections);	
				estimatedTime = System.currentTimeMillis() - startTime;
				if (estimatedTime > 200) System.err.println("\n THIRD STEP [MS] = " + estimatedTime);
			}
			
			estimatedTime = System.currentTimeMillis() - globalStartTime;
			if (estimatedTime > 200) System.err.println("TOTAL TIME IN [MS] = " + estimatedTime);
		}
		
	}
	
	public void draw() {
		
		p.image(currentImg, 0,0, 200, 150);
		if (imageProcessed != null) p.image(imageProcessed, 0,200, 200, 150);
		if (intersections != null) {
			if (intersections.size() >= 4) {
				p.image(resultImage, 0,400, 570, 400);
			}
		}
	
		
	}
	
	public PVector getAngles() {
		return rotationAngles;
	}
	
	
}

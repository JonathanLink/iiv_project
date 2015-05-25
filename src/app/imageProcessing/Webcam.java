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
	private final static boolean INTERACTION_MODE = true;
	public final static int PROCESS_EACH_X_FRAME = 1;

	public static float scale = 0.5f;

	private PImage currentImg;
	private ImageProcessingSolver processing;
	private DetectionSolver detection;
	private TwoDThreeD converter;

	private PImage imageProcessed;
	private List<PVector> intersections; 
	private PVector rotationAngles;

	private Capture cam;
	private PGraphics resultImage;

	public Webcam(PApplet parent) {
		super(parent);

		currentImg = p.loadImage(BOARD_IMAGE);

		if (INTERACTION_MODE) {
			String[] cameras = Capture.list(); 
			cam = new Capture(parent, cameras[3]); 
			cam.start();
		} 
		rotationAngles = new PVector();
		resultImage = p.createGraphics((int)(scale*currentImg.width), (int)(scale*currentImg.height),PApplet.P2D);

	}

	public void update() {

		if (INTERACTION_MODE) {
			if (cam.available()) {
				cam.read();
			}
			currentImg = cam.get();
		} 

		if (p.frameCount % PROCESS_EACH_X_FRAME == 0) { 
			//initialize the solvers
			processing = new ImageProcessingSolver(p);
			detection = new DetectionSolver(p, resultImage);
			converter = new TwoDThreeD((int)(MainController.WINDOW_WIDTH*scale),(int)(MainController.WINDOW_HEIGHT*scale));
		
			//System.out.println("******************************************************************************************************************************");
			long startTime = System.currentTimeMillis();	
			// 1st step
			imageProcessed =  processing.solve(currentImg);
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.err.println("Time to image processing = " + estimatedTime);

			

			// 2nd step
			//Get the (x,y) coordinates of the corners after the detection algorithms	
			long startTime2 = System.currentTimeMillis();	
			intersections = detection.solve(imageProcessed);
			long estimatedTime2 = System.currentTimeMillis() - startTime2;
			System.err.println("Time to detection = " + estimatedTime2);

			// 3th step
			if (intersections!= null && intersections.size() >= 4) {
				rotationAngles = converter.get3DRotations(intersections);	
			}
		
		}

	}

	public void draw() {

		p.image(currentImg, 0,500,(int)(scale*currentImg.width), (int)(scale*currentImg.height));
		if (imageProcessed != null) p.image(imageProcessed, 0,500, (int)(scale*currentImg.width), (int)(scale*currentImg.height));
		if (intersections != null) {
			if (intersections.size() >= 4) {
				p.image(resultImage, 0,500, (int)(scale*currentImg.width), (int)(scale*currentImg.height));
			}
		}


	}

	public PVector getAngles() {
		return rotationAngles;
	}


}

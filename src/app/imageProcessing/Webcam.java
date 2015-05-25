package app.imageProcessing;

import java.util.List;

import app.Parent;
import app.controllers.MainController;
import app.controllers.PlateController;
import app.imageProcessing.solver.DetectionSolver;
import app.imageProcessing.solver.ImageProcessingSolver;
import app.imageProcessing.solver.TwoDThreeD;
import app.views.objects.Plate;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

public class Webcam extends Parent{

	private static final String BOARD_IMAGE= "1.png"; 
	private final static boolean INTERACTION_MODE = true;
	public final static int PROCESS_EACH_X_FRAME = 10;

	public static float scale = 0.35f;
	private PlateController plateController;
	private float linearIncrementX;
	private float linearIncrementZ;


	private PImage currentImg;
	private ImageProcessingSolver processing;
	private DetectionSolver detection;
	private TwoDThreeD converter;

	private PImage imageProcessed;
	private List<PVector> intersections; 
	private PVector rotationAngles;

	private Capture cam;
	private PGraphics resultImage;
	
	public int drawWidth;

	public Webcam(PApplet parent, PlateController plateController) {
		super(parent);
		this.plateController = plateController;
		linearIncrementX = 0;
		linearIncrementZ = 0;

		currentImg = p.loadImage(BOARD_IMAGE);

		if (INTERACTION_MODE) {
			String[] cameras = Capture.list(); 
			cam = new Capture(parent, cameras[3]); 
			cam.start();
		} 
		rotationAngles = new PVector(0,0,0);
		resultImage = p.createGraphics((int)(scale*currentImg.width), (int)(scale*currentImg.height),PApplet.P2D);

	}

	public void update() {

		if (INTERACTION_MODE) {
			if (cam.available()) {
				cam.read();
			}
			currentImg = cam.get();
		} 
		
		drawWidth = (int) (scale*currentImg.width);
		
		if (p.frameCount % PROCESS_EACH_X_FRAME == 0) { 
			System.out.println("******************************************************************************************************************************");
			//initialize the solvers
			processing = new ImageProcessingSolver(p);
			detection = new DetectionSolver(p, resultImage);
			converter = new TwoDThreeD((int)(MainController.WINDOW_WIDTH*scale),(int)(MainController.WINDOW_HEIGHT*scale));

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


				float pastX = plateController.plate.angleX ;
				float pastZ = plateController.plate.angleZ;
				float newX = rotationAngles.x;
				float newZ = rotationAngles.y;
				float distanceX;
				float distanceZ;

				System.err.println("-----");
				System.err.println("pastX: " + pastX);
				System.err.println("newX: " + newX);
				System.err.println("pastZ: " + pastZ);
				System.err.println("newZ: " + newZ);

				if (pastX*newX > 0 ) {
					distanceX = Math.abs(Math.abs(newX) - Math.abs(pastX));
				} else {
					distanceX = Math.abs(Math.abs(newX) + Math.abs(pastX));
				} 

				if (pastZ*newZ > 0 ) {
					distanceZ = Math.abs(Math.abs(newZ) - Math.abs(pastZ));
				} else {
					distanceZ = Math.abs(Math.abs(newZ) + Math.abs(pastZ));
				} 

				System.err.println("distanceX: " + distanceX);
				System.err.println("distanceZ: " + distanceZ);

				if(distanceX < 15) {
					linearIncrementX = 0;
				} else {
					linearIncrementX = distanceX / PROCESS_EACH_X_FRAME;
					if (pastX > newX) {
						linearIncrementX = -linearIncrementX;
					}
				}		

				if(distanceZ < 15) {
					linearIncrementZ = 0;
				} else {
					linearIncrementZ = distanceZ / PROCESS_EACH_X_FRAME;


					if (pastZ > newZ) {
						linearIncrementZ = -linearIncrementZ;
					}
				}
				System.err.println("linearIncrementX: " + linearIncrementX);
				System.err.println("linearIncrementZ: " + linearIncrementZ);
			}
		}

	}

	public void draw(int posx, int posy) {
		
		p.image(currentImg,posx, posy,drawWidth, (int)(scale*currentImg.height));
		if (imageProcessed != null) p.image(imageProcessed,posx,posy, (int)(scale*currentImg.width), (int)(scale*currentImg.height));
		if (intersections != null) {
			if (intersections.size() >= 4) {
				p.image(resultImage, posx,posy, drawWidth, (int)(scale*currentImg.height));
			}
		}


	}

	public PVector getAngles() {
		return rotationAngles;
	}

	public float getIncrementX() {
		return linearIncrementX;
	}

	public float getIncrementZ() {
		return linearIncrementZ;
	}


}

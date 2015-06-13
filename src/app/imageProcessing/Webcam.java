package app.imageProcessing;

import java.util.List;

import app.Parent;
import app.controllers.MainController;
import app.controllers.PlateController;
import app.imageProcessing.solver.DetectionSolver;
import app.imageProcessing.solver.ImageProcessingSolver;
import app.imageProcessing.solver.TwoDThreeD;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.*;

public class Webcam extends Parent{

	public static int PROCESS_EACH_X_FRAME = 10;

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
	private float factorIncrement;

	private Capture cam;
	private Movie mov;
	private PGraphics resultImage;

	public int drawWidth;
	public int drawHeight;
	
	private int counterNoQuad;

	public Webcam(PApplet parent, PlateController plateController) {
		super(parent);
		this.plateController = plateController;
		linearIncrementX = 0;
		linearIncrementZ = 0;
		counterNoQuad = 0;
		
		if (PlateController.DEMO) {
			PROCESS_EACH_X_FRAME = 1;
			factorIncrement = 0.1f;
		} else {
			PROCESS_EACH_X_FRAME = 10;
			factorIncrement = 0.3f;
		}

		if (!PlateController.DEMO) {
			String[] cameras = Capture.list(); 
			cam = new Capture(parent, cameras[3]);
		} else {
			mov = new Movie(parent, "src/data/testvideo.mp4");
		}
		rotationAngles = new PVector(0,0,0);
	}

	public PImage getImage() {
		if (!PlateController.DEMO) {
			if (cam.available()) {
				cam.read();
			}
			return cam.get();
		} else {
			return mov.get();
		}
	}

	public void start() {
		if (!PlateController.DEMO) {
			cam.start();
		} else {
			mov.loop();
		}
	}

	public void stop() {
		if (!PlateController.DEMO) {
			cam.stop();
		} else {
			mov.stop();
		}
	}


	public void update() {
		
		//if it is the sixth time that we don't detect any quads, we reset the position of the plate
		if (counterNoQuad > 6) {
			plateController.resetPlatePosition();
		}

		if (!PlateController.DEMO) {
			if (cam.available()) {
				cam.read();
			}
			currentImg = cam.get();
		} else {
			if (mov.available()) {
				mov.read();
			}
			currentImg = mov.get();
		}

		drawWidth = (int) (scale*currentImg.width);
		drawHeight = (int)(scale*currentImg.height);
		
		if (resultImage == null) {
			resultImage = p.createGraphics((int)(scale*currentImg.width), (int)(scale*currentImg.height),PApplet.P2D);
		}

		if (p.frameCount % PROCESS_EACH_X_FRAME == 0 && currentImg != null) { 
			//initialize the solvers
			processing = new ImageProcessingSolver(p, scale);
			detection = new DetectionSolver(p, plateController, resultImage, scale);
			converter = new TwoDThreeD((int)(MainController.WINDOW_WIDTH*scale),(int)(MainController.WINDOW_HEIGHT*scale));
			
			// 1st step
			imageProcessed =  processing.solve(currentImg);

			// 2nd step
			//Get the (x,y) coordinates of the corners after the detection algorithms	
			intersections = detection.solve(imageProcessed);
						
			// 3th step
			if (intersections!= null && intersections.size() >= 4) {
				counterNoQuad = 0;
				rotationAngles = converter.get3DRotations(intersections);

				float pastX = plateController.plate.angleX ;
				float pastZ = plateController.plate.angleZ;
				float newX = rotationAngles.x;
				float newZ = rotationAngles.y;
				float distanceX;
				float distanceZ;

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

				if(distanceX < 15) {
					linearIncrementX = 0;
				} else {
					linearIncrementX = factorIncrement*distanceX / PROCESS_EACH_X_FRAME;
					if (pastX > newX) {
						linearIncrementX = -linearIncrementX;
					}
				}		

				if(distanceZ < 15) {
					linearIncrementZ = 0;
				} else {
					linearIncrementZ = factorIncrement*distanceZ / PROCESS_EACH_X_FRAME;

					if (pastZ > newZ) {
						linearIncrementZ = -linearIncrementZ;
					}
				}
			} else {
				counterNoQuad += 1;
			}
		}

	}

	public void draw(int posx, int posy) {
		
		p.pushMatrix();
		p.scale(-1,1);
		if(currentImg != null) p.image(currentImg,-posx-drawWidth, posy,drawWidth, drawHeight);
		if (imageProcessed != null) p.image(imageProcessed,-posx-drawWidth,posy,drawWidth, drawHeight);
		/*if (intersections != null) {
			if (intersections.size() >= 4) {
				p.image(resultImage, -posx-drawWidth,posy, drawWidth, drawHeight);
			}
		}*/
		p.popMatrix();
		
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

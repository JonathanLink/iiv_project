package app.controllers;
import app.layers.ConsoleLayer;
import app.layers.DataVisualizationLayer;
import processing.core.*;
import processing.event.MouseEvent;


public class MainController extends PApplet {
	
	public final static int FRAME_RATE = 30;
	public final static boolean DEBUG_MODE = true;
	public final static int PLATE_MODE = 0;
	public final static int EDIT_MODE = 1;
	
	// Console
	public static ConsoleLayer consoleLayer; // consoleLayer must be accessible from anywhere.

	// Controllers
	private PlateController plateController;
	private EditController editController;
	private Controller currentController;
	// Layers
	private DataVisualizationLayer dataVisualizationLayer;
	// SerialVersionUID (to please to Eclipse...)
	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "app.controllers.MainController" });
	}

	public void setup() {
		size(displayWidth, displayHeight, P3D);
		frameRate(FRAME_RATE);

		// Init plateController
		plateController = new PlateController(this);

		// Init EditController
		editController = new EditController(this, plateController);

		// Set current mode
		this.setMode(PLATE_MODE);

		// Set data visualization layer
		dataVisualizationLayer = new DataVisualizationLayer(this, plateController); 

		// Set console
		consoleLayer = new ConsoleLayer(this);
	}
	
	public void draw() {
		cursor(ARROW);

		setCamera();
		setLight();
		setBackground();

		currentController.draw();
		if (DEBUG_MODE) consoleLayer.draw();
		dataVisualizationLayer.draw();

	}
	
	public void keyPressed() {
		if (key == CODED && keyCode == SHIFT) {
			this.setMode(EDIT_MODE);
		}
		currentController.keyPressed();
	}

	public void keyReleased() {
		if (key == CODED && keyCode == SHIFT) {
			this.setMode(PLATE_MODE);
		}
		currentController.keyReleased();
	}

	public void mouseDragged() {
		currentController.mouseDragged();
	}

	public void mouseWheel(MouseEvent event) {
		currentController.mouseWheel(event);
	}

	public void mouseClicked() {
		currentController.mouseClicked();
	}


	private void setCamera() {
		camera();
	}

	private void setLight() {
		directionalLight(50, 100, 125, 0, -1, 0);
		ambientLight(200, 200, 200);
	}

	private void setBackground() {
		background(255.0f, 255.0f, 255.0f);
	}

	private void setMode(int mode) {
		switch(mode) {
		case PLATE_MODE:
			currentController = plateController; 
			break;
		case EDIT_MODE:
			currentController = editController;
			break;
		default:
			println("[ERROR] MODE DOES NOT EXIST!");
		}
	}

	

}

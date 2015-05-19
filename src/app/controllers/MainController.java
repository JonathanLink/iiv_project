package app.controllers;
import app.layers.ConsoleLayer;
import processing.core.*;
import processing.event.MouseEvent;


public class MainController extends PApplet {

	public final static int FRAME_RATE = 30;
	public final static int PLATE_MODE = 0;
	public final static int EDIT_MODE = 1;
	public final static int MENU_MODE = 2;
	public final static int SETTINGS_MODE = 3;
	
	public static boolean debug = false;
	public static boolean webcamEnabled = false;
	
	// Console
	public static ConsoleLayer consoleLayer; // consoleLayer must be accessible from anywhere.

	// Controllers
	private static SettingsController settingsController;
	private static MainMenuController menuController;
	private static PlateController plateController;
	private static EditController editController;
	private static Controller currentController;

	// SerialVersionUID (to please to Eclipse...)
	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "app.controllers.MainController" });
	}
	
	public static void setMode(int mode) {
		switch(mode) {
		case PLATE_MODE:
			currentController = plateController; 
			break;
		case EDIT_MODE:
			currentController = editController;
			break;
		case MENU_MODE:
			currentController = menuController;
			break;
		case SETTINGS_MODE:
			currentController = settingsController;
			break;
		default:
			println("[ERROR] MODE DOES NOT EXIST!");
		}
		currentController.init();
	}
	
	public static Controller getCurrentControler() {
		return currentController;
	}

	public void setup() {
		size(displayWidth, displayHeight, P3D);
		//size(1280, 800, P3D);
		frameRate(FRAME_RATE);
		
		// Set console
		consoleLayer = new ConsoleLayer(this);
		
		// Init MenuController
		menuController = new MainMenuController(this);
		
		// Init settingsController
		settingsController = new SettingsController(this);
		
		// Init plateController
		plateController = new PlateController(this);

		// Init EditController
		editController = new EditController(this, plateController);
		
		// Set current mode
		MainController.setMode(SETTINGS_MODE);

	}

	public void draw() {
		cursor(ARROW);

		setCamera();
		setBackground();	

		//Do not change the order of the draw functions : first the window displays (layers), second the game display (plate,ball)
		pushMatrix();
		currentController.draw();
		popMatrix();
		if (debug) {
			consoleLayer.draw();
		}
	}

	public void keyPressed() {
		final int LETTER_D = 68;
		final int LETTER_M = 77;
		if (keyCode == LETTER_D) {
			debug = !debug;
		} else if (keyCode == LETTER_M) {
			MainController.setMode(MENU_MODE);
		}
		currentController.keyPressed();
	}

	public void keyReleased() {
		/*
		if (key == CODED && keyCode == SHIFT) {
			MainController.setMode(PLATE_MODE);
		}
		currentController.keyReleased();
		*/
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
	
	public void mousePressed() {
		currentController.mousePressed();
	}

	private void setCamera() {
		camera();
	}


	private void setBackground() {
		background(255.0f, 255.0f, 255.0f);
	}




}

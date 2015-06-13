package app.controllers;

import app.layers.ConsoleLayer;
import processing.core.*;
import processing.event.MouseEvent;


public class MainController extends PApplet {
	
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 800;
	
	public final static int FRAME_RATE = 30;
	public final static int PLATE_VIEW = 0;
	public final static int EDIT_VIEW = 1;
	public final static int MENU_VIEW = 2;
	public final static int SETTINGS_VIEW = 3;
	public final static int MODE_VIEW= 4;
	
	public static boolean debug = false;
	public static boolean webcamEnabled = false;
	
	// Console
	public static ConsoleLayer consoleLayer; // consoleLayer must be accessible from anywhere.

	// Controllers
	private static SettingsController settingsController;
	private static MainMenuController menuController;
	private static PlateController plateController;
	private static EditController editController;
	private static Controller modeController;
	private static Controller currentController;

	// SerialVersionUID (to please to Eclipse...)
	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present","--bgcolor=#000000", "--hide-stop", "app.controllers.MainController" });
	}
	
	public static void setMode(int mode) {
		if (currentController !=null) currentController.bye();
		
		switch(mode) {
		case PLATE_VIEW:
			currentController = plateController; 
			break;
		case EDIT_VIEW:
			currentController = editController;
			break;
		case MENU_VIEW:
			currentController = menuController;
			break;
		case SETTINGS_VIEW:
			currentController = settingsController;
			break;
		case MODE_VIEW:
			currentController = modeController;
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
		//size(displayWidth, displayHeight, P3D)
		size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);	
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
		
		// Init ModeController
		modeController = new ModeController(this);
		
		// Set current mode
		MainController.setMode(SETTINGS_VIEW);
		
	}

	public void draw() {
		
		currentController.update();
		
		cursor(ARROW);
		
		setCamera();
		setBackground();	
		
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
			MainController.setMode(MENU_VIEW);
		}
		currentController.keyPressed();
	}

	public void keyReleased() {
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

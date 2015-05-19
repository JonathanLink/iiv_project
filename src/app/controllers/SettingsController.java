package app.controllers;

import processing.core.PApplet;
import processing.core.PImage;
import app.listener.ButtonListener;
import app.views.button.Button;
import app.views.button.MenuButton;

public class SettingsController extends Controller implements ButtonListener{
	
	private static final String BACKGROUND_FILE = "settingsBackground.png";
	private static final String SETTINGS_TITLE_FILE = "settingsTitle.png";
	
	private PImage settingsTitleImage;
	private PImage backgroundImage;
	private MenuButton mouseButton;
	private MenuButton webcamButton;
	
	public SettingsController(PApplet parent) {
		super(parent);
		backgroundImage = p.loadImage(BACKGROUND_FILE);
		settingsTitleImage = p.loadImage(SETTINGS_TITLE_FILE);
		mouseButton = new MenuButton(p, 400, p.displayHeight * 0.40f, 400, 100, "> MOUSE", this);
		webcamButton = new MenuButton(p, 400, mouseButton.y + mouseButton.height + 40, 400, 100, "> WEBCAM", this);
		
	}


	@Override
	public void draw() {
		p.background(0, 0, 0);
		p.image(backgroundImage, 0, 0, p.displayWidth, p.displayHeight);
		p.image(settingsTitleImage, PApplet.round(p.displayWidth/2.0f - settingsTitleImage.width/2.0f), 0);
		mouseButton.draw();
		webcamButton.draw();
	}
	
	@Override
	public void mousePressed() {
		mouseButton.mousePressed();
		webcamButton.mousePressed();
	}
	
	@Override
	public void buttonPressed(Button button) {
		
		if (button == mouseButton) {
			MainController.webcamEnabled = false;
		} else {
			MainController.webcamEnabled = true;
		}
		
		MainController.setMode(MainController.MENU_MODE);
		
	}

}

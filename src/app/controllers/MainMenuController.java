package app.controllers;



import app.listener.ButtonListener;
import app.views.button.Button;
import app.views.button.MenuButton;
import processing.core.PApplet;
import processing.core.PImage;

public class MainMenuController extends Controller implements ButtonListener {
	
	private static final String BACKGROUND_MENU_FILE = "mainMenuBackground.png";
	private static final String MENU_TITLE_FILE = "menuTitle.png";
	private static final String BURGER_SODA_FIRES_FILE = "burgerSodaFries.png";
	private static final String MENU_RAYS_FILE = "menuRays.png";
	
	private PImage backgroundImage;
	private PImage menuTitleImage;
	private PImage burgerSodaFriesImage;
	private PImage menuRaysImage;
	
	private MenuButton playButton;
	private MenuButton editButton;
	private MenuButton creditButton;
	
	private float screenRatio;
	private float rayAngle;
			
	public MainMenuController(PApplet parent) {
		super(parent);
		backgroundImage = p.loadImage(BACKGROUND_MENU_FILE);
		menuTitleImage = p.loadImage(MENU_TITLE_FILE);
		burgerSodaFriesImage = p.loadImage(BURGER_SODA_FIRES_FILE);
		menuRaysImage = p.loadImage(MENU_RAYS_FILE);
		playButton = new MenuButton(p, 170, p.height * 0.35f, 400, 90, "> PLAY", this);
		editButton = new MenuButton(p, 170, playButton.y + playButton.height + 40, 400, 90, "> EDIT", this);
		creditButton = new MenuButton(p, 170, playButton.y + playButton.height + 40 + editButton.height + 40, 400, 90, "> CREDITS", this);
	}
	
	@Override
	public void draw() {
		p.background(0, 0, 0);
		p.image(backgroundImage, 0, 0, p.width, p.height);
		p.image(menuTitleImage, p.width * 0.5f - menuTitleImage.width * 0.5f, 10 * screenRatio);
		p.pushMatrix();
		float dx = 250;
		float dy = 200;
		setOrigin(p.width/2.0f + dx, p.height/2.0f + dy, 0); 
		p.rotateZ(rayAngle);
		p.image(menuRaysImage,-menuRaysImage.width/2.0f , -menuRaysImage.height/2.0f);
		p.popMatrix();
		rayAngle += 0.01;
		p.image(burgerSodaFriesImage, p.width - burgerSodaFriesImage.width - 10, p.height - burgerSodaFriesImage.height - 10);
		playButton.draw();
		editButton.draw();
		creditButton.draw();
		
	}
	

	@Override
	public void mousePressed() {
		playButton.mousePressed();
		editButton.mousePressed();
	}
	
	@Override
	public void buttonPressed(Button button) {
		if (button == playButton) {
			MainController.setMode(MainController.MODE_VIEW);
		} else if (button == editButton) {
			MainController.setMode(MainController.EDIT_VIEW);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}

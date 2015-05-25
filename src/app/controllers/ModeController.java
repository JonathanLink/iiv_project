package app.controllers;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import app.listener.ButtonListener;
import app.views.button.Button;
import app.views.button.MenuButton;
import app.views.obstacles.PlateObstacleObject;
import app.views.obstacles.PlateObstacleType;

public class ModeController extends Controller implements ButtonListener {
	
	private static final String BACKGROUND_FILE = "modeBackground.png";
	private static final String MODE_TITLE_FILE = "modeTitle.png";
	
	private PImage backgroundImage;
	private PImage modeTitleImage;
	private MenuButton classicButton;
	private MenuButton eatAllButton;
	
	public ModeController(PApplet parent) {
		super(parent);
		backgroundImage = p.loadImage(BACKGROUND_FILE);
		modeTitleImage = p.loadImage(MODE_TITLE_FILE);
		classicButton = new MenuButton(p, 400, p.height * 0.42f, 400, 100, "> CLASSIC", this);
		eatAllButton = new MenuButton(p, 400, classicButton.y + classicButton.height + 40, 400, 100, "> EAT ALL", this);
	}

	@Override
	public void draw() {
		p.background(0, 0, 0);
		p.image(backgroundImage, 0, 0, p.width, p.height);
		p.image(modeTitleImage, PApplet.round(p.width/2.0f - modeTitleImage.width/2.0f), 0);
		classicButton.draw();
		eatAllButton.draw();
	}
	
	@Override
	public void mousePressed() {
		classicButton.mousePressed();
		eatAllButton.mousePressed();
	}

	@Override
	public void buttonPressed(Button button) {
		
		MainController.setMode(MainController.PLATE_VIEW);
		PlateController plateController = (PlateController) MainController.getCurrentControler();
		
		if (button == classicButton) {
			plateController.setGameMode(PlateController.GameMode.CLASSIC); 
			loadDefaultPlateObstacles();
		} else {
			plateController.setGameMode(PlateController.GameMode.EAT_ALL);
			MainController.setMode(MainController.EDIT_VIEW);
		}
		
	}
	
	private void loadDefaultPlateObstacles() {
		
		PlateController plateController = (PlateController) MainController.getCurrentControler();
		ArrayList<PlateObstacleObject> plateObstacleObjects = new ArrayList<PlateObstacleObject>();
		
		// Create a burger
		PlateObstacleObject burger = plateController.constructPlateObstacleObject(PlateObstacleType.BURGER); 
		burger.location.x = 0;
		burger.location.z = -50;
		plateObstacleObjects.add(burger);
		
		// Create a burger
		PlateObstacleObject burger2 = plateController.constructPlateObstacleObject(PlateObstacleType.BURGER); 
		burger2.location.x = 150;
		burger2.location.z = -150;
		plateObstacleObjects.add(burger2);
		
		// Create a moving drink
		PlateObstacleObject movingDrink = plateController.constructPlateObstacleObject(PlateObstacleType.MOVING_DRINK); 
		movingDrink.location.x = -10;
		movingDrink.location.z = 100;
		plateObstacleObjects.add(movingDrink);
		
		// Load the obstacles
		plateController.loadPlateObstacles(plateObstacleObjects);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}

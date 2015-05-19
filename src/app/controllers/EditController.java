package app.controllers;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import app.listener.ButtonListener;
import app.views.objects.Ball;
import app.views.objects.Plate;
import app.views.obstacles.Cylinder;
import app.views.obstacles.PlateObstacleObject;
import app.views.obstacles.PlateObstacleType;
import app.views.button.Button;
import app.views.button.MenuButton;

class EditController extends Controller implements ButtonListener {
	
	private static final String BACKGROUND_FILE = "editBackground.png";
	private static final String EDIT_TITLE_FILE = "editTitle.png";
	
	private ArrayList<PlateObstacleObject> obstacles;
	private PImage editTitleImage;
	private PImage backgroundImage;
	private Plate plate;
	private Ball ball;
	private PlateController plateController;
	private PlateObstacleType currentPlateObstacleObject;
	

	// buttons
	private Button burgerButton;
	private Button movingBurgerButton;
	private Button friesButton;
	private Button movingFriesButton;
	private Button drinkButton;
	private Button movingDrinkButton;
	private Button clearButton;
	private MenuButton cancelButton;
	private MenuButton playButton;
	
	//private Text selectedObstacleText;

	public EditController(PApplet parent, PlateController plateController) {
		super(parent);
		this.plate = plateController.plate;
		this.ball = plateController.ball;
		this.plateController = plateController;
		
		// init plate obstacle object
		obstacles = new ArrayList<PlateObstacleObject>();
		currentPlateObstacleObject = PlateObstacleType.BURGER;
		
		// init buttons
		float xMargin = 35;
		burgerButton = new Button(p, xMargin ,250, 150, 35,"Burger", this);
		friesButton = new Button(p, xMargin ,250 + burgerButton.height, 150, 35,"Fries", this);
		drinkButton = new Button(p, xMargin ,250 + burgerButton.height + friesButton.height, 150, 35,"Drink", this);
		movingBurgerButton = new Button(p, xMargin , 250 + burgerButton.height + friesButton.height + drinkButton.height, 250, 35, "Moving Burger", this);
		movingFriesButton = new Button(p, xMargin , 250 + burgerButton.height + friesButton.height + drinkButton.height + movingBurgerButton.height,250, 35,"Moving Fries", this);
		movingDrinkButton = new Button(p, xMargin,250 + burgerButton.height + friesButton.height + drinkButton.height + movingBurgerButton.height + movingFriesButton.height,250, 35,"Moving Drink", this);
		clearButton = new Button(p, xMargin,500,250, 35,"CLEAR", this);
		
		// buttons
		backgroundImage = p.loadImage(BACKGROUND_FILE);
		editTitleImage = p.loadImage(EDIT_TITLE_FILE);
		cancelButton = new MenuButton(p, 0, p.displayHeight * 0.90f, 400, 80, "CANCEL", this);
		playButton = new MenuButton(p, p.displayWidth - 200, p.displayHeight * 0.90f, 400, 80, "PLAY!", this);
		
	}
	
	public void init() {
		plateController.removeAllPlateObstacles();
	}

	public void draw() {
		
		p.background(0, 0, 0);
		p.image(backgroundImage, 0, 0, p.displayWidth, p.displayHeight);
		p.image(editTitleImage, PApplet.round(p.displayWidth/2.0f - editTitleImage.width/2.0f), 0);
		cancelButton.draw();
		playButton.draw();
		
		setOrigin(p.displayWidth/2.0f, p.displayHeight/2.0f, 0); 

		//draw the plate in 2D
		p.pushMatrix();
		p.translate(-plate.width/2.0f, -plate.depth/2.0f);
		plate.draw2D();
		p.popMatrix();

		//draw the ball in 2D
		ball.draw2D();

		//draw all obstacles in 2D
		for (PlateObstacleObject obstacle : obstacles) {
			obstacle.draw2D();
		}

		//test if the mouse click is on the plate to display the cursor
		if (isMouseInsidePlate()) {
			p.cursor(PApplet.CROSS);
		}
		
		// draw buttons	
		setOrigin(-p.displayWidth/2.0f, -p.displayHeight/2.0f, 0); 
		burgerButton.draw();
		movingBurgerButton.draw();
		friesButton.draw();
		movingFriesButton.draw();
		drinkButton.draw();
		movingDrinkButton.draw();
		clearButton.draw();
		
	}
	
	public void mouseClicked() {
		//test if the mouse click is on the plate
		if (isMouseInsidePlate()) {
			PVector mouseLocation = new PVector(getCoordX(), 0, getCoordY());
			//Prevent to add an obstacle on the ball
			if (!isBallClear(mouseLocation)) return;
			//Prevent to add an obstacle which could overlapp another obstacle
			if (!isObstacleClear(mouseLocation)) return;
			// Add the obstacle
			PlateObstacleObject obstacle = plateController.constructPlateObstacleObject(currentPlateObstacleObject); 
			obstacle.location.x = mouseLocation.x;
			obstacle.location.z = mouseLocation.z;
			obstacles.add(obstacle);
		}
	}
	
	public void mousePressed() {
		burgerButton.mousePressed();
		movingBurgerButton.mousePressed();
		friesButton.mousePressed();
		movingFriesButton.mousePressed();
		drinkButton.mousePressed();
		movingDrinkButton.mousePressed();
		cancelButton.mousePressed();
		playButton.mousePressed();
		clearButton.mousePressed();
	}

	
	@Override
	public void buttonPressed(Button button) {
		if (button == burgerButton) {
			currentPlateObstacleObject = PlateObstacleType.BURGER;
		} else if (button == movingBurgerButton) {
			currentPlateObstacleObject = PlateObstacleType.MOVING_BURGER;
		} else if (button == friesButton) {
			currentPlateObstacleObject = PlateObstacleType.FRIES;
		} else if (button == movingFriesButton) {
			currentPlateObstacleObject = PlateObstacleType.MOVING_FRIES;
		} else if (button == drinkButton) {
			currentPlateObstacleObject = PlateObstacleType.DRINK;
		} else if (button == movingDrinkButton) {
			currentPlateObstacleObject = PlateObstacleType.MOVING_DRINK;
		} else if (button == clearButton) {
			obstacles.removeAll(obstacles);
		} else if (button == cancelButton) {
			MainController.setMode(MainController.MENU_MODE);
		} else if (button == playButton) {
			MainController.setMode(MainController.PLATE_MODE);
			PlateController plateController = (PlateController) MainController.getCurrentControler();
			plateController.loadPlateObstacles(obstacles);
		}
	}
	

	private boolean isMouseInsidePlate() {
		return (getCoordX() < plate.width/2 &&
				getCoordX() > -plate.width/2 &&
				getCoordY() > -plate.depth/2 &&
				getCoordY() < plate.depth/2);
	}

	private boolean isBallClear(PVector mouseLocation) {
		PVector distanceToTheBall = PVector.sub(mouseLocation, ball.location); 
		distanceToTheBall.y = 0; 
		if (distanceToTheBall.mag() < Cylinder.CYLINDER_RADIUS + ball.radius) { 
			MainController.consoleLayer.write("ERROR: you cannot add a cylinder here,it would overlap with the ball");
			return false;
		}
		return true;
	}

	private boolean isObstacleClear(PVector mouseLocation) {
		for (PlateObstacleObject obstacle : obstacles) {
			PVector distanceBetweenCylinders = PVector.sub(mouseLocation, obstacle.location);      
			if (obstacle.isCylindric() && distanceBetweenCylinders.mag() < 2.0*Cylinder.CYLINDER_RADIUS ) {
				return false;
			}
		}
		return true;
	}
	
	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	private int getCoordX() {
		return PApplet.round(p.mouseX - p.displayWidth/2.0f);
	}

	private int getCoordY() {
		return PApplet.round(p.mouseY - p.displayHeight/2.0f);
	}


	


}

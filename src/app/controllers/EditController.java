package app.controllers;


import java.util.Date;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import app.controllers.PlateController.GameMode;
import app.imageProcessing.Webcam;
import app.imageProcessing.solver.BlobDetection;
import app.imageProcessing.solver.DetectionSolver;
import app.imageProcessing.solver.ImageProcessingSolver;
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
	private final int TIME_FOR_SAVING_CORNERS = 5;
	private final int TIME_FOR_TAKING_BLOB_PHOTO = 0;
	private static float SCALE = 0.5f;

	private PImage backgroundImage;
	private Plate plate;
	private Ball ball;
	private PlateController plateController;
	private PlateObstacleType currentPlateObstacleObject;
	private boolean blobIsEnabled;
	private Webcam webcam;
	private PImage webcamImage;
	private PImage blobResultImage;
	PGraphics cornersImage;
	private BlobDetection blobDetection;
	private long startTime;
	private boolean blobPictureHasBeenShot;
	private boolean cornersHaveBeenSaved;
	private PImage imageProcessed;


	// buttons
	private Button burgerButton;
	private Button movingBurgerButton;
	private Button friesButton;
	private Button movingFriesButton;
	private Button drinkButton;
	private Button movingDrinkButton;
	private Button clearButton;
	private Button blobButton;
	private MenuButton cancelButton;
	private MenuButton playButton;


	//private Text selectedObstacleText;

	public EditController(PApplet parent, PlateController plateController) {
		super(parent);
		this.plateController = plateController;

		currentPlateObstacleObject = PlateObstacleType.BURGER;

		// init buttons
		float xMargin = 35;
		int y;
		int yMargin = 5;
		y = 250;
		burgerButton = new Button(p, xMargin ,250, 150, 35,"Burger", this);
		y += burgerButton.height + yMargin;
		friesButton = new Button(p, xMargin ,y, 150, 35,"Fries", this);
		y += friesButton.height + yMargin;
		drinkButton = new Button(p, xMargin ,y, 150, 35,"Drink", this);
		y +=  drinkButton.height + yMargin;
		movingBurgerButton = new Button(p, xMargin ,  y, 250, 35, "Moving Burger", this);
		y +=  movingBurgerButton.height + yMargin;
		movingFriesButton = new Button(p, xMargin ,  y,250, 35,"Moving Fries", this);
		y +=  movingFriesButton.height + yMargin ;
		movingDrinkButton = new Button(p, xMargin, y ,250, 35,"Moving Drink", this);
		blobButton = new Button(p, xMargin,500,250, 35,"BLOB ON/OFF", this);
		clearButton = new Button(p, xMargin,500 + 40,250, 35,"CLEAR", this);

		// buttons
		backgroundImage = p.loadImage(BACKGROUND_FILE);
		cancelButton = new MenuButton(p, 45, p.height * 0.90f - 25, 400, 80, "MENU", this);
		playButton = new MenuButton(p, p.width - 210, p.height * 0.90f - 25, 400, 80, "PLAY!", this);

		// webcam
		webcam = new Webcam(parent, plateController);

	}

	public void init() {
		if (plateController.gameMode == GameMode.CLASSIC) {
			//plateController.removeAllPlateObstacles();
		} 
		this.plate = plateController.plate;
		this.ball = plateController.ball;
		this.blobIsEnabled = false;
	}

	int counter = 0;
	boolean showBlobResult = false;
	public void draw() {

		p.background(0, 0, 0);
		p.image(backgroundImage, 0, 0, p.width, p.height);
		//p.image(editTitleImage, PApplet.round(p.width/2.0f - editTitleImage.width/2.0f), 0);
		cancelButton.draw();
		playButton.draw();

		setOrigin(p.width/2.0f, p.height/2.0f, 0); 

		//draw the plate in 2D
		p.pushMatrix();
		p.translate(-plate.width/2.0f, -plate.depth/2.0f);
		plate.draw2D();
		p.popMatrix();

		//draw the ball in 2D
		ball.draw2D();

		//draw all obstacles in 2D
		for (PlateObstacleObject obstacle : plateController.obstacleList) {
			obstacle.draw2D();
		}

		//test if the mouse click is on the plate to display the cursor
		if (isMouseInsidePlate()) {
			p.cursor(PApplet.CROSS);
		}

		// draw buttons	
		setOrigin(-p.width/2.0f, -p.height/2.0f, 0); 
		burgerButton.draw();
		movingBurgerButton.draw();
		friesButton.draw();
		movingFriesButton.draw();
		drinkButton.draw();
		movingDrinkButton.draw();
		if(MainController.webcamEnabled && !PlateController.DEMO) {
			blobButton.draw();
		}
		clearButton.draw();

		// draw webcam image
		if (blobIsEnabled) {
 
			if (blobPictureHasBeenShot) {
				if (blobResultImage != null) p.image(blobResultImage, 0, 0, (int)(cornersImage.width), (int)(cornersImage.height));
			} else {
				p.image(webcamImage, 0, 0, (int)(SCALE*webcamImage.width), (int)(SCALE*webcamImage.height));

			}
			if (imageProcessed!=null) p.image(imageProcessed, 0, 0, (int)(SCALE*imageProcessed.width), (int)(SCALE*imageProcessed.height));
		}

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
			plateController.obstacleList.add(obstacle);
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
		blobButton.mousePressed();
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
		} else if (button == blobButton) {
			blobIsEnabled = !blobIsEnabled;
			if (blobIsEnabled) {
				blobPictureHasBeenShot = false;
				cornersHaveBeenSaved = false;
				cornersImage = null;
				blobResultImage = null;
				startTime = (new Date()).getTime()/1000;
				webcam.start();
			} else {
				webcam.stop();
			}
		} else if (button == clearButton) {
			plateController.removeAllPlateObstacles();
		} else if (button == cancelButton) {
			MainController.setMode(MainController.MENU_VIEW);
		} else if (button == playButton) {
			MainController.setMode(MainController.PLATE_VIEW);
			//PlateController plateController = (PlateController) MainController.getCurrentControler();
			//plateController.loadPlateObstacles(obstacles);
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
		for (PlateObstacleObject obstacle : plateController.obstacleList) {
			PVector distanceBetweenCylinders = PVector.sub(mouseLocation, obstacle.location);      
			if (obstacle.isCylindric() && distanceBetweenCylinders.mag() < 2.0*Cylinder.CYLINDER_RADIUS ) {
				return false;
			}
		}
		return true;
	}

	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	private int getCoordX() {
		return PApplet.round(p.mouseX - p.width/2.0f);
	}

	private int getCoordY() {
		return PApplet.round(p.mouseY - p.height/2.0f);
	}

	@Override
	public void update() {
		if (blobIsEnabled) {
			long currentTime = (new Date()).getTime()/1000;
			if (!cornersHaveBeenSaved && currentTime - startTime >= TIME_FOR_SAVING_CORNERS) {
				webcamImage = webcam.getImage();
				ImageProcessingSolver processing = new ImageProcessingSolver(p, SCALE);
				cornersImage = p.createGraphics((int)(SCALE*webcamImage.width), (int)(SCALE*webcamImage.height),PApplet.P2D);
				DetectionSolver detection = new DetectionSolver(p, plateController, cornersImage, SCALE);
				imageProcessed =  processing.solve(webcamImage);
				List<PVector> corners = detection.solve(imageProcessed);
				if (corners.size() == 4) {
					blobDetection = new BlobDetection(p, corners.get(0), corners.get(1), corners.get(2), corners.get(3));
					cornersHaveBeenSaved = true;
				}
				startTime =  (new Date()).getTime()/1000;
			} else if (!blobPictureHasBeenShot && cornersHaveBeenSaved && currentTime - startTime >= TIME_FOR_TAKING_BLOB_PHOTO) {
				int yellowHueLowerBound = 0;
				int yellowHueUpperBound = 35;
				webcamImage = blobDetection.colorThresholding(webcamImage, yellowHueLowerBound, yellowHueUpperBound);
				blobResultImage = blobDetection.findConnectedComponents(webcamImage);
				addObstaclesFromTheBlobList(blobDetection.getBlobCenters());
				blobPictureHasBeenShot = true;
			} else if (!cornersHaveBeenSaved){
				webcamImage = webcam.getImage(); 
			} else if (blobPictureHasBeenShot && currentTime - startTime >= 5) {
				blobIsEnabled = false;
			}
		}
	}

	private void addObstaclesFromTheBlobList(List<PVector> centers) {
		plateController.removeAllPlateObstacles(); // clear the board first
		currentPlateObstacleObject = PlateObstacleType.BURGER;

		for (int i = 0; i < centers.size(); ++i) {

			PVector blob = centers.get(i);

			int x = (int)( Math.round( ( (   blob.x   / blobDetection.getLengthBoardSize() ) * plate.width)) );
			int y = (int)( Math.round( ( (   blob.y   / blobDetection.getLengthBoardSize() ) * plate.depth)) );

			// offset
			x = (int)Math.round(x - p.width/5.0f);
			y = (int)Math.round(y - p.height/4.0f);

			PlateObstacleObject obstacle = plateController.constructPlateObstacleObject(currentPlateObstacleObject); 
			obstacle.location.x = x;
			obstacle.location.z = y;
			plateController.obstacleList.add(obstacle);

		}
	}





}

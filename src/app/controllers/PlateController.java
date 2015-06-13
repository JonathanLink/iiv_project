package app.controllers;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import app.imageProcessing.Webcam;
import app.layers.DataVisualizationLayer;
import app.listener.AnimatedTextListener;
import app.views.objects.Ball;
import app.views.objects.Plate;
import app.views.objects.PlateObject;
import app.views.objects.texts.AnimatedTextPlate;
import app.views.objects.texts.PointsText;
import app.views.objects.texts.StartCountdown;
import app.views.obstacles.Cylinder;
import app.views.obstacles.MovingCylinder;
import app.views.obstacles.PlateEdges;
import app.views.obstacles.PlateObstacleObject;
import app.views.obstacles.PlateObstacleType;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

public class PlateController extends Controller implements AnimatedTextListener{

	public enum GameMode {
		CLASSIC, EAT_ALL
	}

	public static final boolean DEMO = true; //TODO Change to FALSE if you want to test the webcam (--> you will need to change the thresholds in ImageProcessingSolver.java)
	public static final int COUNTDOWN = 60;
	public static final boolean START_COUNTER = true;
	public Plate plate;
	public Ball ball;
	public ArrayList<PlateObstacleObject> obstacleList;
	public int totalScore;
	public float gainPoints;
	public boolean locked;
	public GameMode gameMode;

	private static final float GRAVITY_CONST_MOUSE = 0.05f; 
	private static final float FRICTION_COEF_MOUSE = 0.005f;

	private static float GRAVITY_CONST_WEBCAM = 0.3f;  
	private static float FRICTION_COEF_WEBCAM = 0.001f;

	private static final float LIMIT_ANGLE = 60.0f; //angle in degree
	private static final float SPEED_MIN = 0.5f;
	private static final float SPEED_MAX = 1.5f;
	private static final float SPEED_STEP = 0.05f;
	private static final int NUMBER_OF_OBSTACLES_EAT_MODE = 5;
	private static final float TOP_VIEW_SCALE = 0.2f;
	private static final String BURGER_OBJ_FILE_PATH = "burger.obj";
	private static final String FRIES_OBJ_FILE_PATH = "fries.obj";
	private static final String DRINK_OBJ_FILE_PATH = "drink.obj";
	private static final String LOGO_SMALL_FILE = "logoSmall.png";

	// banners
	/*private static final String BANNER_WAKE_UP= "banners/bannerwakeUp.png";
	private PImage wakeUpBanner;
	private long wakeUpLastTimePopUp;*/

	private  float gravityCoef; 
	private  float frictionCoef;
	private ArrayList<AnimatedTextPlate> animatedTextList;
	private ArrayList<PlateObstacleObject> obstaclesToRemoveList;
	private DataVisualizationLayer dataVisualizationLayer;
	public PlateEdges plateEdges;
	private int countdown;
	private long lastTimeDown;
	private int countup;
	private double lastTimeUp;
	private StartCountdown startCountDown3;
	private StartCountdown startCountDown2;
	private StartCountdown startCountDown1;
	private StartCountdown startCountDownGo;
	private boolean gameStart;
	private ArrayList<AnimatedTextPlate> futureAnimatedTextList;
	private int numberOfHit;
	private Webcam webcam;
	public boolean gameIsFinished;
	private boolean isInputEnabled;
	private PImage logoImage;
	private PGraphics topView;
	private boolean fiveHitDone;
	private long lastTimeScored;

	public PlateController(PApplet parent) {
		super(parent);
		this.obstacleList = new ArrayList<PlateObstacleObject>();
		this.animatedTextList = new ArrayList<AnimatedTextPlate>();
		this.futureAnimatedTextList = new ArrayList<AnimatedTextPlate>();
		this.obstaclesToRemoveList = new ArrayList<PlateObstacleObject>();
		this.gameMode = GameMode.CLASSIC;
		webcam = new Webcam(parent, this);
		logoImage = p.loadImage(LOGO_SMALL_FILE);
		
		if (DEMO) {
			GRAVITY_CONST_WEBCAM =  0.85f;  
		}
		// banners
		//this.wakeUpBanner = p.loadImage(BANNER_WAKE_UP);
	}

	public void init() {
		if (MainController.webcamEnabled) webcam.start();
	}

	public void bye() {
		if (MainController.webcamEnabled) webcam.stop();
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;

		this.gravityCoef = (MainController.webcamEnabled) ? GRAVITY_CONST_WEBCAM : GRAVITY_CONST_MOUSE;
		this.frictionCoef = (MainController.webcamEnabled) ? FRICTION_COEF_WEBCAM : FRICTION_COEF_MOUSE;

		this.plate = (gameMode == GameMode.EAT_ALL) ? new Plate(p, 600, 600, 1.75f,-257, -10, -183) : new Plate(p, 500, 500, 1.45f, -214, -10, -150);
		this.ball = (gameMode == GameMode.EAT_ALL) ?  new Ball(p, this, 10) : new Ball(p, this, 20);
		this.plateEdges = new PlateEdges(p, this);
		this.dataVisualizationLayer = new DataVisualizationLayer(p, this); 
		this.plate.angleX = 0;
		this.plate.angleZ = 0;
		this.ball.angleX = 0;
		this.ball.angleZ = 0;
		this.ball.location.x = 0;
		this.ball.location.z = 0;
		this.totalScore = 0;
		this.countdown = COUNTDOWN;
		this.countup = 0;
		this.numberOfHit = 0;
		this.gameStart = false;
		this.gameIsFinished = false;
		this.isInputEnabled = true;
		this.fiveHitDone = false;
		this.animatedTextList.removeAll(animatedTextList);
		this.futureAnimatedTextList.removeAll(futureAnimatedTextList);
		this.topView = p.createGraphics(PApplet.round(plate.width * TOP_VIEW_SCALE), PApplet.round(plate.depth * TOP_VIEW_SCALE), PApplet.P2D);

		removeAllPlateObstacles();	



		// Start 3-2-1-GO coutdown 
		if (START_COUNTER) {
			startCountDown3 = new StartCountdown(p, this, "3", 0, 0, 1000);
			addAnimatedTextPlate(startCountDown3);
		} else {
			this.lastTimeScored = (new Date()).getTime()/1000;
			gameStart = true;
		}

	}



	public void loadPlateObstacles(ArrayList<PlateObstacleObject> plateObstacleObjects) {
		this.removeAllPlateObstacles();
		for (PlateObstacleObject obstacle : plateObstacleObjects) {
			this.addPlateObstacle(obstacle);
		}
	}

	public void removeAllPlateObstacles() {
		obstacleList.removeAll(obstacleList);
		this.addPlateObstacle(plateEdges); // but keep edges of the board
	}

	public void addAnimatedTextPlate(AnimatedTextPlate animatedTextPlate) {
		animatedTextList.add(animatedTextPlate);
	}

	public void addPoints(float gainPoints, PlateObstacleObject obstacle) {
		if (!gameIsFinished) {
			this.gainPoints = gainPoints;
			totalScore += (gainPoints * 100);
			totalScore = (totalScore < 0) ? 0 : totalScore;
			if (gameMode == GameMode.EAT_ALL) {
				if (obstacle != plateEdges) {
					numberOfHit = numberOfHit + 1;
					obstaclesToRemoveList.add(obstacle);
				}
			}
			lastTimeScored = (new Date()).getTime()/1000;
		}
	}

	public void update() {

		if (isGameFinished()) {
			gameIsFinished = true;
			isInputEnabled = false;
			updateAllObjects();
			if(MainController.webcamEnabled) {
				webcam.stop();
			}
		} else {
			if (gameStart && isInputEnabled && MainController.webcamEnabled) {
				webcamInput();
			}
			updateAllObjects();
		}


	}

	public void draw() {

		if(MainController.debug) {
			dataVisualizationLayer.draw();
		}


		displayCountdown();
		setOrigin(p.width/2.0f, p.height/2.0f, 0); //the origin of the plate is at the middle point of the window
		p.image(logoImage, p.width/2.0f - logoImage.width - 10 , p.height/2.0f - logoImage.height - 10 ); // draw logo in bottom right corner
		p.lights();
		renderAllObjects();


		if (gameMode == GameMode.EAT_ALL) {
			p.noLights();
			setOrigin(-p.width/2.0f,- p.height/2.0f, 0);
			displayEatenObstaclesCounter();
			displayScore();
			//displayCountup();
			setOrigin(p.width/2.0f, p.height/2.0f, 0);
		} else {
			p.noLights();
			setOrigin(-p.width/2.0f,- p.height/2.0f, 0);
			displayScore();
			setOrigin(p.width/2.0f, p.height/2.0f, 0);
		}

		if (!fiveHitDone && !gameIsFinished && isAllFiveObstaclesEaten()) {
			//displayMessagePanel("5 HITS!");
			fiveHitDone = true;
			PointsText fiveHit = new PointsText(p, this, "5 HITS!", 0, -200, 0, 100, p.color(255,0,0),1.5f);
			futureAnimatedTextList.add(fiveHit);

		}

		if (gameIsFinished) {
			displayMessagePanel("ROUND FINISHED!");
			plate.angleX = -30;
			plate.angleZ = 0;
		}

		if (MainController.webcamEnabled) {
			int posx = -(int)(webcam.drawWidth*0.5);
			int margin = 5;
			int posy;
			//posy = (plate.angleX > 0.0) ? (int)(MainController.WINDOW_HEIGHT*0.5) -(int)(webcam.drawHeight) - margin : - (int)(MainController.WINDOW_HEIGHT*0.5) + margin;
			posy = - (int)(MainController.WINDOW_HEIGHT*0.5) + margin;

			if(!gameIsFinished) {
				webcam.draw(posx, posy);
			}
		}

		//displayBanner();
	}



	public void mouseWheel(MouseEvent event) {
		float count = event.getCount();
		if (count > 0 && (plate.speed + SPEED_STEP) <= SPEED_MAX) {
			plate.speed += SPEED_STEP;
		} else if (count < 0 && (plate.speed - SPEED_STEP) >= SPEED_MIN) {
			plate.speed -= SPEED_STEP;
		}
	}

	public void mouseDragged() {  
		if (gameStart && isInputEnabled) {
			if (!MainController.webcamEnabled) {
				mouseInput();
			}
		}

	}  


	public void keyPressed() {


		if (p.key == PApplet.CODED && p.keyCode == PApplet.SHIFT) {
			MainController.setMode(MainController.EDIT_VIEW);
		}

		final int LETTER_R = 82;
		final int LETTER_S = 83;
		if (p.keyCode == LETTER_R) {
			resetPlatePosition();
		} 

		if (p.keyCode == LETTER_S) {
			MainController.webcamEnabled = !MainController.webcamEnabled;
			if (MainController.webcamEnabled) {
				webcam.start();
			} else {
				webcam.stop();
			}
		} 

	}

	public PlateObstacleObject constructPlateObstacleObject(PlateObstacleType plateType) {
		float burgerScaleFactor = (float) ((gameMode == GameMode.CLASSIC) ? 1.0f : 20.0f/30.0f);
		float burgerRadius = (float) ((gameMode == GameMode.CLASSIC) ? 30.0f : 20.0f);
		switch(plateType) {
		case BURGER:
			return new Cylinder(p, this, 1.0f * burgerScaleFactor, burgerRadius, 0, 0, p.color(255,128,0) ,BURGER_OBJ_FILE_PATH, 0, 8, 80);
		case MOVING_BURGER:
			return new MovingCylinder(p, this, 1.0f * burgerScaleFactor, burgerRadius, 0, 0, p.color(254,195,139) , BURGER_OBJ_FILE_PATH, 0 ,8,80);
		case FRIES:
			return new Cylinder(p, this, 1.0f , 20, 0, 0, p.color(255,255,0), FRIES_OBJ_FILE_PATH, 0, -20, 100);
		case MOVING_FRIES:
			return new MovingCylinder(p, this, 1.0f, 20, 0, 0, p.color(255,255,136), FRIES_OBJ_FILE_PATH, 0 ,-20, 100);
		case DRINK:
			return new Cylinder(p, this, 1.0f, 20, 0, 0, p.color(255,0,0), DRINK_OBJ_FILE_PATH, 0, -20, 100);
		case MOVING_DRINK:
			return new MovingCylinder(p, this, 1.0f, 20, 0, 0, p.color(252,112,128), DRINK_OBJ_FILE_PATH, 0 ,-20, 100); 
		}

		return null;

	}

	@Override
	public void animatedTextHasFinished(AnimatedTextPlate animatedTextPlate) {
		if (animatedTextPlate == startCountDownGo) {
			gameStart = true;
			this.lastTimeScored = (new Date()).getTime()/1000;
		}
	}

	@Override
	public void animatedTextHasFinishedHalfWay(AnimatedTextPlate animatedTextPlate) {

		if (animatedTextPlate == startCountDown3) {
			startCountDown2 = new StartCountdown(p, this, "2", 0, 0, 1000);
			futureAnimatedTextList.add(startCountDown2);
		} else if (animatedTextPlate == startCountDown2) {
			startCountDown1 = new StartCountdown(p, this, "1",0, 0, 1000);
			futureAnimatedTextList.add(startCountDown1);
		} else if (animatedTextPlate == startCountDown1) {
			PointsText eatAnimatedText = new PointsText(p, this, "EAT!", 0, 150, 150, 400, p.color(255,0,0),5.0f);
			gameStart = true;
			if(gameMode == GameMode.EAT_ALL) {
				futureAnimatedTextList.add(eatAnimatedText);
			}
		} 
	}

	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	public int getCoordX() {
		return PApplet.round(p.mouseX - p.width/2.0f);
	}

	public int getCoordY() {
		return PApplet.round(p.mouseY - p.height/2.0f);
	}



	private boolean isGameFinished() {
		return countdown <= 0;
		/*if (gameMode == GameMode.CLASSIC) {
			return countdown <= 0;
		} else if (gameMode == GameMode.EAT_ALL) {
			return numberOfHit == NUMBER_OF_OBSTACLES_EAT_MODE;
		}
		return true;*/
	}

	private boolean isAllFiveObstaclesEaten() {
		return numberOfHit >= NUMBER_OF_OBSTACLES_EAT_MODE;
	}

	private void displayCountdown() {
		if (gameStart) {
			if (!gameIsFinished && p.millis() - lastTimeDown >= 1000) {
				countdown = countdown - 1;
				lastTimeDown = p.millis();
			}

			int y = 70 ;//p.height - 25;

			if ( PApplet.floor(countdown / 60) > 0) {
				int fontSize = 80; // 30
				p.textSize(fontSize);
				p.textAlign(PApplet.LEFT);
				p.fill(p.color(0,0,0));
				p.text(countdown / 60 , 0, y);
				p.textSize(fontSize-10);
				p.fill(p.color(255,0,0));
				p.text(" m", 35, y);
				p.textSize(fontSize);
				p.fill(p.color(0,0,0));
				int sec = countdown % 60;
				String secString = (String) ((sec < 10) ? "0"+sec : ""+sec);
				p.text(secString, 110, y);
				p.textSize(fontSize-10);
				p.fill(p.color(255,0,0));
				p.text(" sec", 160, y);
			} else {
				p.textAlign(PApplet.LEFT);
				int fontSize = 80; // 30
				p.textSize(fontSize);
				p.fill(p.color(0,0,0));
				int sec = countdown % 60;
				String secString = (String) ((sec < 10) ? "0"+sec : ""+sec);
				p.text(secString, 5, y);
				p.textSize(fontSize-10);
				p.fill(p.color(255,0,0));
				p.text(" sec", 70, y);
			}


		}
	}

	private void displayEatenObstaclesCounter() {
		if (gameStart) {
			float y = p.height -25;
			p.textSize(100);
			p.textAlign(PApplet.LEFT);
			p.fill(p.color(255,0,0));
			p.text(numberOfHit + "/" + NUMBER_OF_OBSTACLES_EAT_MODE ,0, y);
		}
	}

	private void displayScore() {
		if (gameStart) {
			p.textSize(100);
			p.textAlign(PApplet.RIGHT);
			p.fill(0,255,0);
			p.text(totalScore, p.width - 200, 80);
			p.fill(0,0,0);
			p.textSize(50);
			p.text("kcal", p.width - 100, 80);
		}
	}

	private void displayCountup() {
		if (gameStart) {
			if (!gameIsFinished && p.millis() - lastTimeUp >= 1000) {
				countup = countup + 1;
				lastTimeUp = p.millis();
			}
			p.textSize(30);
			p.textAlign(PApplet.LEFT);
			p.fill(p.color(0,0,0));
			p.text(countup / 60 , 0, p.height - 25);
			p.textSize(20);
			p.fill(p.color(255,0,0));
			p.text(" min", 15, p.height - 25);
			p.textSize(30);
			p.fill(p.color(0,0,0));
			int sec = countup % 60;
			String secString = (String) ((sec < 10) ? "0"+sec : ""+sec);
			p.text(secString, 60, p.height - 25);
			p.textSize(20);
			p.fill(p.color(255,0,0));
			p.text(" sec", 85, p.height - 25);
		}
	}

	/*private void displayEndPanel() {
		p.textSize(90);
		p.textAlign(PApplet.CENTER);
		p.fill(255,0,0);
		p.text("ROUND FINISHED!" , 0, -200);
	}

	private void displayFiveHitPanel() {
		p.textSize(90);
		p.textAlign(PApplet.CENTER);
		p.fill(255,0,0);
		p.text("5 HITS!!" , 0, -200);
	}*/

	private void displayMessagePanel(String message) {
		p.textSize(90);
		p.textAlign(PApplet.CENTER);
		p.fill(255,0,0);
		p.text(message , 0, -200);
	}



	private void addPlateObstacle(PlateObstacleObject obstacle) {
		obstacleList.add(obstacle);
	}

	private void updateAllObjects() {
		if (gameStart) {
			plate.update();
			for (PlateObstacleObject obstacle : obstacleList) {
				obstacle.update();
			}
			addGravity(ball);
			addFrictionForce(ball);
			ball.update();
		}
		removeObstaclesToRemove();
		updateAnimatedTextPlate();
		addAnimatedTextPlateFromList();
	}

	private void removeObstaclesToRemove() {
		Iterator<PlateObstacleObject> iterator = obstaclesToRemoveList.iterator();
		while (iterator.hasNext ()) {
			PlateObstacleObject obstacle = iterator.next();
			obstacleList.remove(obstacle);
			iterator.remove();
		}
	}

	private void addAnimatedTextPlateFromList() {
		Iterator<AnimatedTextPlate> iterator = futureAnimatedTextList.iterator();
		while (iterator.hasNext ()) {
			AnimatedTextPlate animatedTextPlate = iterator.next();
			addAnimatedTextPlate(animatedTextPlate);
			iterator.remove();
		}
	}

	private void updateAnimatedTextPlate() {
		Iterator<AnimatedTextPlate> iterator = animatedTextList.iterator();
		while (iterator.hasNext ()) {
			AnimatedTextPlate animatedTextPlate = iterator.next();
			animatedTextPlate.update();
			if (animatedTextPlate.isAnimationFinished()) {
				iterator.remove();
			}
		}
	}

	private void renderAllObjects() {
		plate.render();
		for (PlateObstacleObject obstacle : obstacleList) {
			obstacle.render();
		}
		ball.render();
		for (AnimatedTextPlate animatedTextPlate : animatedTextList) {
			animatedTextPlate.render();
		}
	}

	private void addFrictionForce(PlateObject plateObject) {
		PVector frictionForce = plateObject.generateFrictionForce(frictionCoef);
		plateObject.applyForce(frictionForce);
	}

	private void addGravity(PlateObject plateObject) {
		plateObject.gravity.x = PApplet.sin(PApplet.radians(plate.angleZ)) * gravityCoef;
		plateObject.gravity.z = -PApplet.sin(PApplet.radians(plate.angleX)) * gravityCoef;
		plateObject.applyForce(plateObject.gravity);
	}

	public void resetPlatePosition() {
		ball.acceleration = new PVector(0,0,0);
		ball.velocity = new PVector(0,0,0);
		plate.angleX = 0.0f;
		plate.angleZ = 0.0f;
	}

	private void mouseInput() {
		float x = (float) ((p.mouseX - p.pmouseX) * (LIMIT_ANGLE / ((plate.depth * 1.2) / 2.0) ));
		float y = (float) ((p.mouseY - p.pmouseY) * (LIMIT_ANGLE / ((plate.width * 1.2) / 2.0) ));
		if (p.mouseX <= p.width && p.mouseY <= p.height && !locked) {
			if ((plate.angleZ + x * plate.speed) <= LIMIT_ANGLE && (plate.angleZ + x * plate.speed) >= -LIMIT_ANGLE) {
				plate.angleZ += x * plate.speed;
			} 

			if ((plate.angleX - y * plate.speed ) <= LIMIT_ANGLE && (plate.angleX - y * plate.speed) >= -LIMIT_ANGLE) {
				plate.angleX -= y * plate.speed;
			}
		}
	}




	private void webcamInput() {
		// webcam 
		webcam.update();

		if ((plate.angleX + webcam.getIncrementX()) <= LIMIT_ANGLE && (plate.angleX + webcam.getIncrementX()) >= -LIMIT_ANGLE) {
			plate.angleX += webcam.getIncrementX();
		} 

		if ((plate.angleZ + webcam.getIncrementZ()) <= LIMIT_ANGLE && (plate.angleZ + webcam.getIncrementZ()) >= -LIMIT_ANGLE) {
			plate.angleZ += webcam.getIncrementZ();
		}

	}


	/*private boolean wakeUpTimeIsDisplayed = false;
	private void displayBanner() {
		if (!gameIsFinished) {

			long currentTime = (new Date()).getTime()/1000;

			if ( currentTime - lastTimeScored >= 5) {
				wakeUpTimeIsDisplayed = true;
				if (wakeUpLastTimePopUp == -1) {
					wakeUpLastTimePopUp = 
				}
				if ( currentTime - wakeUpLastTimePopUp < 10) {
					wakeUpLastTimePopUp = currentTime;
					p.image(wakeUpBanner, wakeUpBanner.width/2 , p.height/2 - wakeUpBanner.height);
				}
			} 

		}
	}*/

	private void drawMap(int posx, int posy) {

		topView.beginDraw();
		topView.scale(TOP_VIEW_SCALE);

		//Draw the plate in 2d
		plate.draw2D(topView);
		topView.translate(PApplet.round(plate.width/2.0f), PApplet.round(plate.depth/2.0f)); //set the center of the 2D plate at origin

		//Draw the ball in 2d
		ball.draw2D(topView);

		//Draw the obstacles in 2d
		for (PlateObstacleObject obstacle : obstacleList) {
			obstacle.draw2D(topView);
		}
		topView.endDraw();

		p.image(topView, posx, posy);

	}

}

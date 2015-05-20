package app.controllers;
import java.util.ArrayList;
import java.util.Iterator;

import app.imageProcessing.Webcam;
import app.layers.DataVisualizationLayer;
import app.listener.AnimatedTextListener;
import app.views.objects.Ball;
import app.views.objects.Plate;
import app.views.objects.PlateObject;
import app.views.objects.texts.AnimatedTextPlate;
import app.views.objects.texts.StartCountdown;
import app.views.obstacles.Cylinder;
import app.views.obstacles.MovingCylinder;
import app.views.obstacles.PlateEdges;
import app.views.obstacles.PlateObstacleObject;
import app.views.obstacles.PlateObstacleType;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class PlateController extends Controller implements AnimatedTextListener{

	public enum GameMode {
		CLASSIC, EAT_ALL
	}

	public static final int COUNTDOWN = 15;  // sec
	public Plate plate;
	public Ball ball;
	public ArrayList<PlateObstacleObject> obstacleList;
	public float totalScore;
	public float gainPoints;
	public boolean locked;
	public GameMode gameMode;


	private static final float GRAVITY_CONST = 0.04f;  // 0.05 initially 0.1
	private static final float LIMIT_ANGLE = 60.0f; //angle in degree
	private static final float SPEED_MIN = 0.5f;
	private static final float SPEED_MAX = 1.5f;
	private static final float SPEED_STEP = 0.05f;
	private static final float FRICTION_COEF = 0.001f;
	private static final String BURGER_OBJ_FILE_PATH = "burger.obj";
	private static final String FRIES_OBJ_FILE_PATH = "fries.obj";
	private static final String DRINK_OBJ_FILE_PATH = "drink.obj";

	private ArrayList<AnimatedTextPlate> animatedTextList;
	private DataVisualizationLayer dataVisualizationLayer;
	private PlateEdges plateEdges;
	private int countdown;
	private double lastTime;
	private StartCountdown startCountDown3;
	private StartCountdown startCountDown2;
	private StartCountdown startCountDown1;
	private StartCountdown startCountDownGo;
	private boolean gameStart;
	private ArrayList<AnimatedTextPlate> futureAnimatedTextList;
	private Webcam webcam;

	
	
	public PlateController(PApplet parent) {
		super(parent);
		this.obstacleList = new ArrayList<PlateObstacleObject>();
		this.animatedTextList = new ArrayList<AnimatedTextPlate>();
		this.futureAnimatedTextList = new ArrayList<AnimatedTextPlate>();
		this.gameMode = GameMode.CLASSIC;
		webcam = new Webcam(parent);
		
	}

	public void init() {
		MainController.consoleLayer.write("webcamEnabled = " + MainController.webcamEnabled);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;

		this.plate = (gameMode == GameMode.EAT_ALL) ? new Plate(p, 600, 600, 1.75f,-255, -10, -190) : new Plate(p, 500, 500, 1.45f, -214, -10, -150);
		this.ball = (gameMode == GameMode.EAT_ALL) ?  new Ball(p, this, 10) : new Ball(p, this, 20);
		this.plateEdges = new PlateEdges(p, this);
		this.dataVisualizationLayer = new DataVisualizationLayer(p, this); 
		this.plate.angleX = 0;
		this.plate.angleZ = 0;
		this.ball.angleX = 0;
		this.ball.angleZ = 0;
		this.ball.location.x = 0;
		this.ball.location.z = 0;
		this.totalScore = 0f;
		this.countdown = COUNTDOWN;
		this.gameStart = true;
		this.animatedTextList.removeAll(animatedTextList);
		this.futureAnimatedTextList.removeAll(futureAnimatedTextList);

		removeAllPlateObstacles();

		// Start 3-2-1-GO coutdown 
		startCountDown3 = new StartCountdown(p, this, "3", -100, 0, 1000);
		//addAnimatedTextPlate(startCountDown3);
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

	public void addPoints(float gainPoints) {
		this.gainPoints = gainPoints;
		totalScore += gainPoints;
	}

	public void update() {
		// webcamInput
		if (MainController.webcamEnabled) {
			webcamInput();
		}
		updateAllObjects();
	}

	public void draw() {
		
		if(MainController.debug) {
			dataVisualizationLayer.draw();
		}
		p.camera(p.width/2.0f, p.height/2.0f, (p.height/2.0f) / PApplet.tan(PApplet.PI*30.0f / 180.0f) + 200, p.width/2.0f, p.height/2.0f, 0, 0, 1, 0);
		displayCountdown();
		p.lights();
		setOrigin(p.width/2.0f, p.height/2.0f, 0); //the origin of the plate is at the middle point of the window
		renderAllObjects();
		p.camera();
		
		if (MainController.webcamEnabled) {
			p.noLights();
			webcam.draw();
		}
		

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
		if (gameStart) {
			if (!MainController.webcamEnabled) {
				mouseInput();
			}
		}

	}  


	public void keyPressed() {
		/*if(MainController.webcamEnabled || MainController.debug) {
			final int LETTER_R = 82;
			if (p.keyCode == LETTER_R) {
				resetPlatePosition();
			} 
		}*/
		
		/*if (gameMode == GameMode.EAT_ALL) {
			if (p.key == PApplet.CODED && p.keyCode == PApplet.SHIFT) {
				MainController.setMode(MainController.EDIT_VIEW);
			}
		}*/
		
		final int LETTER_R = 82;
		final int LETTER_I = 73;
		System.out.println(p.keyCode);
		if (p.keyCode == LETTER_R) {
			resetPlatePosition();
		} 
		
		if (p.keyCode == LETTER_I) {
			MainController.webcamEnabled = !MainController.webcamEnabled;
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
			return new Cylinder(p, this,0.9f , 20, 0, 0, p.color(255,255,0), FRIES_OBJ_FILE_PATH, 0, -20, 100);
		case MOVING_FRIES:
			return new MovingCylinder(p, this, 1.0f, 20, 0, 0, p.color(255,255,136), FRIES_OBJ_FILE_PATH, 0 ,-20, 100);
		case DRINK:
			return new Cylinder(p, this, 0.8f, 20, 0, 0, p.color(255,0,0), DRINK_OBJ_FILE_PATH, 0, -20, 100);
		case MOVING_DRINK:
			return new MovingCylinder(p, this, 1.0f, 20, 0, 0, p.color(252,112,128), DRINK_OBJ_FILE_PATH, 0 ,-20, 100); 
		}

		return null;

	}

	@Override
	public void animatedTextHasFinished(AnimatedTextPlate animatedTextPlate) {
		if (animatedTextPlate == startCountDownGo) {
			gameStart = true;
		}
	}

	@Override
	public void animatedTextHasFinishedHalfWay(AnimatedTextPlate animatedTextPlate) {
		if (animatedTextPlate == startCountDown3) {
			startCountDown2 = new StartCountdown(p, this, "2", -100, 0, 1000);
			futureAnimatedTextList.add(startCountDown2);
		} else if (animatedTextPlate == startCountDown2) {
			startCountDown1 = new StartCountdown(p, this, "1", -100, 0, 1000);
			futureAnimatedTextList.add(startCountDown1);
		} else if (animatedTextPlate == startCountDown1) {
			startCountDownGo = new StartCountdown(p, this, "EAT!", -150, 0, 1000);
			gameStart = true;
			futureAnimatedTextList.add(startCountDownGo);
		} 
	}

	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	public int getCoordX() {
		return PApplet.round(p.mouseX - p.width/2.0f);
	}

	public int getCoordY() {
		return PApplet.round(p.mouseY - p.height/2.0f);
	}


	private void displayCountdown() {
		if (gameStart) {
			if (p.millis() - lastTime >= 1000) {
				countdown = countdown - 1;
				lastTime = p.millis();
			}

			int fontSize = (countdown <= 10) ? 250 : 200;
			int fontColor = (countdown <= 10) ? p.color(255,0,0) : p.color(0,0,0);

			p.textSize(fontSize);
			p.textAlign(PApplet.CENTER);
			p.fill(fontColor);
			p.text(""+countdown, p.width/2.0f - 50, 50);
		}
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
		updateAnimatedTextPlate();
		addAnimatedTextPlateFromList();
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
		PVector frictionForce = plateObject.generateFrictionForce(FRICTION_COEF);
		plateObject.applyForce(frictionForce);
	}

	private void addGravity(PlateObject plateObject) {
		plateObject.gravity.x = PApplet.sin(PApplet.radians(plate.angleZ)) * GRAVITY_CONST;
		plateObject.gravity.z = -PApplet.sin(PApplet.radians(plate.angleX)) * GRAVITY_CONST;
		plateObject.applyForce(plateObject.gravity);
	}

	private void resetPlatePosition() {
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
		
		MainController.consoleLayer.write("[MOUSE] angle Y = " + plate.angleY);
		MainController.consoleLayer.write("[MOUSE] angles Z = " + plate.angleZ);

	}

	private void webcamInput() {
		// webcam 
		webcam.update();
		PVector angles = webcam.getAngles();
		
		plate.angleX = angles.x;
		plate.angleZ = angles.z;

		MainController.consoleLayer.write("[WEBCAM] angle Z = " + plate.angleZ);
		MainController.consoleLayer.write("[WEBCAM] angles X = " + plate.angleX);
		MainController.consoleLayer.write("[WEBCAM] angles = " + angles);
		MainController.consoleLayer.write("-----------------------------");
	}



}

package app.controllers;
import java.util.ArrayList;
import java.util.Iterator;

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
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

public class PlateController extends Controller implements AnimatedTextListener{
	
	public static final int COUNTDOWN = 15;  // sec
	public Plate plate;
	public Ball ball;
	public ArrayList<PlateObstacleObject> obstacleList;
	public float totalScore;
	public float gainPoints;
	public boolean locked;

	private static final String TOP_BACKGROUND_FILE = "gameTopBackground.png";
	private static final String BOTTOM_BACKGROUND_FILE = "gameBottomBackground.png";
	private static final String LEFT_BACKGROUND_FILE = "gameLeftBackground.png";
	private static final String RIGHT_BACKGROUND_FILE = "gameRightBackground.png";
	private static final float GRAVITY_CONST = 0.1f;  // 0.05 initially
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
	private PImage topBackgroundImage;
	private PImage bottomBackgroundImage;
	private PImage leftBackgroundImage;
	private PImage rightBackgroundImage;
	
	
	public PlateController(PApplet parent) {
		super(parent);
		this.obstacleList = new ArrayList<PlateObstacleObject>();
		this.animatedTextList = new ArrayList<AnimatedTextPlate>();
		this.plate = new Plate(parent);
		this.ball = new Ball(parent, this);
		this.topBackgroundImage = p.loadImage(TOP_BACKGROUND_FILE);
		this.bottomBackgroundImage = p.loadImage(BOTTOM_BACKGROUND_FILE);
		this.leftBackgroundImage = p.loadImage(LEFT_BACKGROUND_FILE);
		this.rightBackgroundImage = p.loadImage(RIGHT_BACKGROUND_FILE);
		this.plateEdges = new PlateEdges(parent, this);
		this.futureAnimatedTextList = new ArrayList<AnimatedTextPlate>();
		this.dataVisualizationLayer = new DataVisualizationLayer(parent, this); 
	}
	
	public void init() {
		plate.angleX = 0;
		plate.angleZ = 0;
		ball.angleX = 0;
		ball.angleZ = 0;
		ball.location.x = 0;
		ball.location.z = 0;
		this.totalScore = 0f;
		this.countdown = COUNTDOWN;
		this.gameStart = false;
		this.animatedTextList.removeAll(animatedTextList);
		this.futureAnimatedTextList.removeAll(futureAnimatedTextList);
		
		// Start 3-2-1-GO coutdown 
		startCountDown3 = new StartCountdown(p, this, "3", -100, 0, 1000);
		addAnimatedTextPlate(startCountDown3);
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

	public void draw() {
		
		drawBackground();
		
		if(MainController.debug) {
			dataVisualizationLayer.draw();
		}
		displayCountdown();
		p.lights();
		setOrigin(p.displayWidth/2.0f, p.displayHeight/2.0f, 0); //the origin of the plate is at the middle point of the window
		updateAllObjects();
		renderAllObjects();
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
	}  
	
	public void keyPressed() {
		if(MainController.debug) {
			final int LETTER_S = 83;
			if (p.keyCode == LETTER_S) {
				freezeBall();
			} 
		}
	}
	
	public PlateObstacleObject constructPlateObstacleObject(PlateObstacleType plateType) {
		switch(plateType) {
			case BURGER:
				return new Cylinder(p, this, 30, 0, 0, p.color(255,128,0) ,BURGER_OBJ_FILE_PATH, 0, 8, 80);
			case MOVING_BURGER:
				return new MovingCylinder(p, this, 30, 0, 0, p.color(254,195,139) , BURGER_OBJ_FILE_PATH, 0 ,8,80);
			case FRIES:
				return new Cylinder(p, this, 20, 0, 0, p.color(255,255,0), FRIES_OBJ_FILE_PATH, 0, -20, 100);
			case MOVING_FRIES:
				return new MovingCylinder(p, this, 20, 0, 0, p.color(255,255,136), FRIES_OBJ_FILE_PATH, 0 ,-20, 100);
			case DRINK:
				return new Cylinder(p, this, 20, 0, 0, p.color(255,0,0), DRINK_OBJ_FILE_PATH, 0, -20, 100);
			case MOVING_DRINK:
				return new MovingCylinder(p, this, 20, 0, 0, p.color(252,112,128), DRINK_OBJ_FILE_PATH, 0 ,-20, 100); 
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
			startCountDownGo = new StartCountdown(p, this, "GO!", -200, 0, 1000);
			futureAnimatedTextList.add(startCountDownGo);
		} 
	}
	
	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	public int getCoordX() {
		return PApplet.round(p.mouseX - p.displayWidth/2.0f);
	}

	public int getCoordY() {
		return PApplet.round(p.mouseY - p.displayHeight/2.0f);
	}
	
	private void drawBackground() {
		p.image(topBackgroundImage, 0, 0, p.displayWidth, topBackgroundImage.height);
		p.image(bottomBackgroundImage, 0, p.displayHeight - bottomBackgroundImage.height); 
		p.image(leftBackgroundImage, 0, 0); 
		p.image(rightBackgroundImage, p.displayWidth - rightBackgroundImage.width, 0); 
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
			p.text(""+countdown, p.displayWidth/2.0f - 50, 200);
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
	
	private void freezeBall() {
		ball.acceleration = new PVector(0,0,0);
		ball.velocity = new PVector(0,0,0);
		plate.angleX = 0.0f;
		plate.angleZ = 0.0f;
	}


	
	

}

package app.controllers;
import java.util.ArrayList;
import java.util.Iterator;

import app.views.objects.Ball;
import app.views.objects.Plate;
import app.views.objects.PlateObject;
import app.views.objects.texts.AnimatedTextPlate;
import app.views.obstacles.PlateEdges;
import app.views.obstacles.PlateObstacleObject;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class PlateController extends Controller {

	public Plate plate;
	public Ball ball;
	public ArrayList<PlateObstacleObject> obstacleList;
	public float totalScore;
	public float gainPoints;
	public boolean locked;

	
	private static final  float GRAVITY_CONST = 0.05f; 
	private static final float LIMIT_ANGLE = 60.0f; //angle in degree
	private static final float SPEED_MIN = 0.5f;
	private static final float SPEED_MAX = 1.5f;
	private static final float SPEED_STEP = 0.05f;
	private static final float FRICTION_COEF = 0.001f;
	
	private ArrayList<AnimatedTextPlate> animatedTextList;

	public PlateController(PApplet parent) {
		super(parent);
		this.obstacleList = new ArrayList<PlateObstacleObject>();
		this.animatedTextList = new ArrayList<AnimatedTextPlate>();
		this.plate = new Plate(parent);
		this.ball = new Ball(parent, this);
		// add plate edges (4 walls)
		PlateEdges plateEdges = new PlateEdges(parent, this);
		this.addPlateObstacle(plateEdges);
		totalScore = 0f;
	}

	public void addPlateObstacle(PlateObstacleObject obstacle) {
		obstacleList.add(obstacle);
	}

	public void addAnimatedTextPlate(AnimatedTextPlate animatedTextPlate) {
		animatedTextList.add(animatedTextPlate);
	}
	
	public void addPoints(float gainPoints) {
		this.gainPoints = gainPoints;
		totalScore += gainPoints;
	}

	public void draw() {
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

	private void updateAllObjects() {
		plate.update();
		for (PlateObstacleObject obstacle : obstacleList) {
			obstacle.update();
		}
		addGravity(ball);
		addFrictionForce(ball);
		ball.update();
		updateAnimatedTextPlate();
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
	
	//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
	public int getCoordX() {
		return PApplet.round(p.mouseX - p.displayWidth/2.0f);
	}

	public int getCoordY() {
		return PApplet.round(p.mouseY - p.displayHeight/2.0f);
	}
	

}

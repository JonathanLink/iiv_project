package app.controllers;

import processing.core.PApplet;
import processing.core.PVector;
import app.views.objects.Ball;
import app.views.objects.Plate;
import app.views.obstacles.Cylinder;
import app.views.obstacles.MovingCylinder;
import app.views.obstacles.PlateObstacleObject;

class EditController extends Controller {

	private Plate plate;
	private Ball ball;
	private PlateController plateController;
	private final int STATIC_CYLINDER = 0;
	private final int MOVING_CYLINDER = 1;
	private int currentObstacle;

	public EditController(PApplet parent, PlateController plateController) {
		super(parent);
		this.plate = plateController.plate;
		this.ball = plateController.ball;
		this.plateController = plateController;
		currentObstacle = STATIC_CYLINDER;
	}

	public void draw() {
		setOrigin(p.displayWidth/2.0f, p.displayHeight/2.0f, 0); 

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
	}
	
	public void mouseClicked() {
		//test if the mouse click is on the plate
		if (isMouseInsidePlate()) {
			PVector mouseLocation = new PVector(getCoordX(), 0, getCoordY());
			//Prevent to add an obstacle on the ball
			if (!isBallClear(mouseLocation)) return;
			//Prevent to add an obstacle which could overlapp another obstacle
			if (!isObstacleClear(mouseLocation)) return;

			//Create a cylinder with the clicked location as center
			Cylinder cylinder = (Cylinder) ((currentObstacle == STATIC_CYLINDER)? new Cylinder(p, plateController, Cylinder.CYLINDER_RADIUS, mouseLocation.x, mouseLocation.z) : new MovingCylinder(p, plateController, Cylinder.CYLINDER_RADIUS, mouseLocation.x, mouseLocation.z));
			plateController.addPlateObstacle(cylinder);
		}
	}

	public void keyPressed() {
		if (p.key == PApplet.CODED && p.keyCode == PApplet.UP) {
			currentObstacle = STATIC_CYLINDER;
		} else if (p.key == PApplet.CODED && p.keyCode == PApplet.DOWN) {
			currentObstacle = MOVING_CYLINDER;
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
		if (distanceToTheBall.mag() < Cylinder.CYLINDER_RADIUS + ball.radius) { // TODO: optimiser, on aura pas forcement que des formes cylindres (rectangle par ex.)
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
	public int getCoordX() {
		return PApplet.round(p.mouseX - p.displayWidth/2.0f);
	}

	public int getCoordY() {
		return PApplet.round(p.mouseY - p.displayHeight/2.0f);
	}

}

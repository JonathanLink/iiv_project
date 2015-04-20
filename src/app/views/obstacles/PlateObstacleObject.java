package app.views.obstacles;

import app.controllers.PlateController;
import app.views.objects.Ball;
import app.views.objects.PlateObject;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class PlateObstacleObject extends PlateObject {
	
	public PlateObstacleObject(PApplet parent, PlateController plateController) {
		super(parent, plateController);
	}

	public abstract PVector checkForCollisionWithBall(Ball ball);
}
package app.views.obstacles;

import app.controllers.PlateController;
import app.views.objects.Ball;
import app.views.objects.PlateObject;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class PlateObstacleObject extends PlateObject {
	
	protected long lastTimePointEarned;
	
	public PlateObstacleObject(PApplet parent, PlateController plateController) {
		super(parent, plateController);
		this.lastTimePointEarned = 0;
	}

	public abstract PVector checkForCollisionWithBall(Ball ball);
	
	public abstract boolean isCylindric();
	
	
}
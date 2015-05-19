package app.views.obstacles;

import java.util.Date;

import app.controllers.PlateController;
import app.views.objects.Ball;
import app.views.objects.texts.PointsText;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class PlateEdges extends PlateObstacleObject {

	public PlateEdges(PApplet parent, PlateController plateController) {
		super(parent, plateController);
	}


	public PVector checkForCollisionWithBall(Ball ball) {

		PVector normal = new PVector(0, 0, 0);
		
		if (ball.location.x  > plate.width/2.0 - ball.radius) {
			losePoints(ball);
			ball.location.x = (float) (plate.width/2.0 - ball.radius);
			ball.velocity.x *= -0.5;
			normal.x = ball.velocity.x;
		} else if (ball.location.x < -plate.width/2.0 + ball.radius) {
			losePoints(ball);
			ball.location.x = (float) (-plate.width/2.0 + ball.radius);
			ball.velocity.x *= -0.5;
			normal.x = ball.velocity.x;
		}

		if (ball.location.z  > plate.depth/2.0 - ball.radius) {
			losePoints(ball);
			ball.location.z = (float) (plate.depth/2.0 - ball.radius);
			ball.velocity.z *= -0.5;
			normal.z = ball.velocity.z;
		} else if (ball.location.z < -plate.depth/2.0 + ball.radius) {
			losePoints(ball);
			ball.location.z = (float) (-plate.depth/2.0 + ball.radius);
			ball.velocity.z *= -0.5;
			normal.z = ball.velocity.z;
		}
		
		
		
		return normal;
	}
	
	
	public void draw2D(PGraphics pGraphics) {

	}

	public boolean isCylindric() {
		return false;
	}
	
	protected void renderObject() {
	}
	
	
	private void losePoints(Ball ball) {
		Date date = new Date();
		long currentTime = date.getTime() / 1000; 
		if (currentTime - lastTimePointEarned >= 0.5) {
			float velocity = PApplet.ceil(ball.velocity.mag());
			if (velocity > 0) {
				lostPointsText(velocity, ball.location.x, ball.location.z);
				plateController.addPoints(velocity * -1);
			}
			lastTimePointEarned = currentTime;
		}	
		
	}
	
	private void lostPointsText(float velocity, float x, float z) {
		plateController.addPoints(PApplet.round(velocity));
		String text = "-"+ PApplet.round(velocity) * 100 +" kCal";
		PointsText pointsText = new PointsText(p, plateController, text, x, -plateController.plate.height , z);
		pointsText.fontSize = 70;
		pointsText.fontFillColor = p.color(255, 0, 0);
		pointsText.fontStrokeColor = p.color(253, 131, 137);
		plateController.addAnimatedTextPlate(pointsText);
	}

	
}

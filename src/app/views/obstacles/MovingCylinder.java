package app.views.obstacles;

import java.util.Date;

import processing.core.PApplet;
import processing.core.PVector;
import app.controllers.PlateController;
import app.views.objects.Ball;

public class MovingCylinder extends Cylinder {

	private boolean leftToRight;
	private float cylinderYAngle;

	public MovingCylinder(PApplet parent, PlateController plateController) {
		super(parent, plateController);
		init();
	}

	public MovingCylinder(PApplet parent,PlateController plateController, float scaleFactor, float radius, float centerCoordX, float centerCoordZ, int fillColor, String model, float rotateX, float bottomMargin, float height) {
		super(parent, plateController, scaleFactor, radius, centerCoordX, centerCoordZ, fillColor, model, rotateX, bottomMargin, height);
		init();
	}

	
	protected void updateObject() {
		super.updateObject();
		
		if (leftToRight) {
			if (location.x + velocity.x < plate.width/2.0 - CYLINDER_RADIUS - 2 * radius) {
				location.x = location.x + velocity.x;
				//location.y = 20.0f * PApplet.cos(1.0f/10.0f * p.frameCount) - 20; // décommente cette ligne si tu veux faire sautiller le cylindre ^^
			} else {
				leftToRight = false;
				velocity.x = velocity.x * - 1.0f;
			}
		}
		if (!leftToRight) {
			if (location.x + velocity.x > -plate.width/2.0 + CYLINDER_RADIUS + 2 * radius) {
				location.x = location.x + velocity.x;
			} else { 
				leftToRight = true;
				velocity.x = velocity.x * - 1.0f;
			}
		}
		
	
	}
	
	protected void renderObject() {
		p.pushMatrix();
		p.rotateY(cylinderYAngle);
		cylinderYAngle += PApplet.PI / 180.0f;
		p.shape(cylinder);
		p.popMatrix();
	}

	private void init() {
		leftToRight = true;
		this.velocity = new PVector(1, 0, 0);
	}

	public boolean isCylindric() {
		return true;
	}
	
	public PVector checkForCollisionWithBall(Ball ball) {
		PVector distanceBetweenCenters = PVector.sub(ball.location, location);    
		distanceBetweenCenters.y = 0;

		//test for collisions
		if (distanceBetweenCenters.mag() < ball.radius + radius ) {
			
			PVector ballDeltaLocation = distanceBetweenCenters.get();
			ballDeltaLocation.setMag((ball.radius + radius) - distanceBetweenCenters.mag());
			ball.location.add(ballDeltaLocation);
		
			plateController.addPoints(PApplet.round(ball.velocity.mag()));

			// user win some points proportioned to the velocity of the ball
			if (ball.velocity.mag() >= MIN_VELOCITY_COLLISION && PApplet.round(PVector.angleBetween(ball.velocity, this.velocity)) != 0.0) {
				Date date = new Date();
				long currentTime = date.getTime() / 1000; 
				if (currentTime - lastTimePointEarned >= 0.5) {
					addPointsText(ball.velocity.mag());
					lastTimePointEarned = currentTime;
				}	
			}

			//normalize the vector given by the two centers at the time of the collision
			distanceBetweenCenters.normalize();
			//create the normal vector
			PVector normal = distanceBetweenCenters.get();
			//compute the cos angle between velocity and normal
			if (ball.velocity.mag() > MIN_VELOCITY_COLLISION) {
				float cos = normal.dot(ball.velocity) / (normal.mag() * ball.velocity.mag() );
				
				//test if the velocity heads to the cylinder and update the velocity
				if (cos < 0) {
					float angle = 2 * normal.dot(ball.velocity);
					PVector temp = PVector.mult(normal, angle);
					//computation of the new velocity
					ball.velocity = PVector.sub(ball.velocity, temp);
					//lost of energy after hitting the cylinder
					ball.velocity = PVector.mult(ball.velocity, 0.5f);
					if (ball.velocity.mag() < this.velocity.mag()) {
						ball.velocity = this.velocity.get();
					}
				}
				
			} else {
				ball.velocity = this.velocity.get();
			}
			

			return normal; 
			
		} else {
			
			return new PVector(0, 0, 0);
		} 
		
		
	}
	

}

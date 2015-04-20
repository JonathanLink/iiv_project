package app.views.obstacles;

import processing.core.PApplet;
import processing.core.PVector;
import app.controllers.PlateController;
import app.views.objects.Ball;

public class MovingCylinder extends Cylinder {

	private boolean leftToRight;

	public MovingCylinder(PApplet parent, PlateController plateController) {
		super(parent, plateController);
		init();
	}

	public MovingCylinder(PApplet parent,PlateController plateController, float radius, float centerCoordX, float centerCoordZ) {
		super(parent, plateController, radius, centerCoordX, centerCoordZ, parent.color(255, 10, 25));
		init();
	}

	
	protected void updateObject() {
		super.updateObject();
		
		if (leftToRight) {
			if (location.x + velocity.x < plate.width/2.0 - CYLINDER_RADIUS - 2 * Ball.BALL_RADIUS) {
				location.x = location.x + velocity.x;
				//location.y = 40.0 * sin(1.0/10.0 * frameCount) - 1 + 40; // décommente cette ligne si tu veux faire sautiller le cylindre ^^
			} else {
				leftToRight = false;
				velocity.x = velocity.x * - 1.0f;
			}
		}
		if (!leftToRight) {
			if (location.x + velocity.x > -plate.width/2.0 + CYLINDER_RADIUS + 2 * Ball.BALL_RADIUS) {
				location.x = location.x + velocity.x;
			} else { 
				leftToRight = true;
				velocity.x = velocity.x * - 1.0f;
			}
		}
		
		//MainController.consoleLayer.write("location="+location);
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
			
			PVector vec = distanceBetweenCenters.get();
			vec.setMag((ball.radius + radius) - distanceBetweenCenters.mag());
			ball.location.add(vec);
		
			plateController.addPoints(PApplet.round(ball.velocity.mag()));

			// user win some points proportioned to the velocity of the ball
			if (ball.velocity.mag() >= MIN_VELOCITY_COLLISION && PApplet.round(PVector.angleBetween(ball.velocity, this.velocity)) != 0.0) {
				if (fillColor != collisionColor) {
					previousColor = fillColor;
					fillColor = collisionColor;
				}
				addPointsText(ball.velocity.mag());
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
				fillColor = previousColor;
				ball.velocity = this.velocity.get();
			}
			

			return normal; 
			
		} else {
			
			return new PVector(0, 0, 0);
		} 
		
		
	}
	

}

package app.views.obstacles;

import app.views.objects.Ball;
import app.views.objects.texts.PointsText;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import app.controllers.*;

public class Cylinder extends PlateObstacleObject {

	public static final int CYLINDER_RADIUS = 25;
	public static final int CYLINDER_HEIGHT = 50;

	protected PShape cylinder;
	protected float radius;
	protected int fillColor;

	private int previousColor;
	private int collisionColor;
	private static final int CYLINDER_RESOLUTION = 40;
	private static final float MIN_VELOCITY_COLLISION = 0.55f;
	

	public Cylinder(PApplet parent, PlateController plateController, float radius, float centerCoordX, float centerCoordZ, int fillColor) {
		super(parent, plateController);
		this.radius = radius;
		this.fillColor = fillColor;
		this.collisionColor = p.color(255, 255, 255);
		this.previousColor = this.fillColor;
		cylinder = new PShape();
		buildCylinder();
		location.x = centerCoordX;
		location.z = centerCoordZ;
	}

	public Cylinder(PApplet parent, PlateController plateController, float radius, float centerCoordX, float centerCoordZ) {
		this(parent, plateController, radius, centerCoordX, centerCoordZ, parent.color(70, 220, 30));
	}

	public Cylinder(PApplet parent, PlateController plateController) {
		this(parent, plateController, Cylinder.CYLINDER_RADIUS, 0, 0);
	}

	public PVector checkForCollisionWithBall(Ball ball) {
		PVector distanceBetweenCenters = PVector.sub(ball.location, location);    
		distanceBetweenCenters.y = 0;

		//test for collisions
		if (distanceBetweenCenters.mag() < ball.radius + radius ) {
			
			plateController.addPoints(PApplet.round(ball.velocity.mag()));

			// user win some points proportioned to the velocity of the ball
			if (ball.velocity.mag() >= MIN_VELOCITY_COLLISION) {
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
			float cos = normal.dot(ball.velocity)/(normal.mag()*ball.velocity.mag());

			//test if the velocity heads to the cylinder and update the velocity
			if (cos < 0) {
				float angle = 2*normal.dot(ball.velocity);
				PVector temp = PVector.mult(normal, angle);
				//computation of the new velocity
				ball.velocity = PVector.sub(ball.velocity, temp);
				//lost of energy after hitting the cylinder
				ball.velocity = PVector.mult(ball.velocity, 0.5f);
			}
			return normal;
			
		} else {
			this.fillColor = previousColor;
			return new PVector(0, 0, 0);
		}
	}

	public void draw2D(PGraphics pGraphics) {
		pGraphics.stroke(fillColor);
		pGraphics.fill(fillColor);
		pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
	}

	public String toString() {
		return "Cylinder with radius = " + radius ;
	}

	protected void updateObject() {
		super.updateObject();
	}

	protected void renderObject() {
		p.noStroke();
		p.fill(fillColor);
		p.shape(cylinder);
	}


	private void buildCylinder() {
		float angle;
		float[] x = new float[CYLINDER_RESOLUTION + 1];
		float[] z = new float[CYLINDER_RESOLUTION + 1];
		for (int i = 0; i < x.length; ++i) {
			angle = (PApplet.TWO_PI / CYLINDER_RESOLUTION) * i;
			x[i] = PApplet.sin(angle) * radius;
			z[i] = PApplet.cos(angle) * radius;
		}

		cylinder = p.createShape();

		cylinder.beginShape(PApplet.TRIANGLE_FAN);
		for (int i=0; i<x.length; i++) {
			cylinder.vertex(0, -CYLINDER_HEIGHT, 0);
			cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i]);
		}
		cylinder.endShape();

		cylinder.beginShape(PApplet.TRIANGLE_FAN);
		for (int i=0; i<x.length; i++) {
			cylinder.vertex(0, -this.plate.height/2, 0);
			cylinder.vertex(x[i], -this.plate.height/2, z[i] );
		}
		cylinder.endShape();

		cylinder.beginShape(PApplet.QUAD_STRIP); 
		for (int i = 0; i < x.length; ++i) {
			cylinder.vertex(x[i], PApplet.round(-plate.height/2.0f), z[i] );
			cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i] );
		} 
		cylinder.endShape();

		cylinder.disableStyle();
	}


	private void addPointsText(float velocity) {
		String text = "+"+ PApplet.round(velocity)+" PTS!";
		PointsText pointsText = new PointsText(p, plateController, text, location.x, location.y - CYLINDER_HEIGHT, location.z);
		plateController.addAnimatedTextPlate(pointsText);
	}
	
	public boolean isCylindric() {
		return true;
	}


}

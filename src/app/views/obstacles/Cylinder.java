package app.views.obstacles;

import java.util.Date;

import app.views.objects.Ball;
import app.views.objects.texts.PointsText;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import app.controllers.*;
import app.controllers.PlateController.GameMode;

public class Cylinder extends PlateObstacleObject {

	public static final int CYLINDER_RADIUS = 25;
	public static final int CYLINDER_HEIGHT = 50;

	protected PShape cylinder;
	protected float radius;
	protected int fillColor;
	protected float height;
	protected static final int CYLINDER_RESOLUTION = 40;
	protected static final float MIN_VELOCITY_COLLISION = 1.0f;
	
	
	public Cylinder(PApplet parent, PlateController plateController, float scaleFactor, float radius, float centerCoordX, float centerCoordZ, int fillColor, String model, float rotateX, float bottomMargin, float height) {
		super(parent, plateController);
		this.radius = radius;
		this.fillColor = fillColor;
		this.height = height;
		cylinder = new PShape();
		if(model == null) {
			buildCylinder();	
		} else {
			cylinder = p.loadShape(model);
			cylinder.scale(scaleFactor);
			cylinder.rotateZ(-PApplet.PI);
			cylinder.rotateX(rotateX);
			cylinder.translate(0, plate.height + bottomMargin);
		}

		location.x = centerCoordX;
		location.z = centerCoordZ;
	}

	public Cylinder(PApplet parent, PlateController plateController, float scaleFactor, float radius, float centerCoordX, float centerCoordZ, String model, float rotateX, float bottomMargin, float height) {
		this(parent, plateController,scaleFactor , radius, centerCoordX, centerCoordZ, parent.color(70, 220, 30), model, rotateX, bottomMargin, height);
	}

	public Cylinder(PApplet parent, PlateController plateController) {
		this(parent, plateController,1.0f, Cylinder.CYLINDER_RADIUS, 0, 0, parent.color(70, 220, 30), null, 0, 0,CYLINDER_HEIGHT );
	}

	public PVector checkForCollisionWithBall(Ball ball) {
		PVector distanceBetweenCenters = PVector.sub(ball.location, location);    
		distanceBetweenCenters.y = 0;

		//test for collisions
		if (distanceBetweenCenters.mag() < ball.radius + radius ) {
			
			if (plateController.gameMode == GameMode.EAT_ALL) {
				if (!plateController.gameIsFinished) addPointsText(ball.velocity.mag());
				return new PVector(0, 0, 0);
			}
			
			// user win some points proportioned to the velocity of the ball
			if (ball.velocity.mag() >= MIN_VELOCITY_COLLISION) {
				Date date = new Date();
				long currentTime = date.getTime() / 1000; 
				if (currentTime - lastTimePointEarned >= 0.5) {
					if (!plateController.gameIsFinished) {
						addPointsText(ball.velocity.mag());
						lastTimePointEarned = currentTime;
					}
					
				}	
			}

			//normalize the vector given by the two centers at the time of the collision
			distanceBetweenCenters.normalize();
			//create the normal vector
			PVector normal = distanceBetweenCenters.get();
			//compute the cos angle between velocity and normal
			float cos = normal.dot(ball.velocity) / (normal.mag() * ball.velocity.mag() );

			//test if the velocity heads to the cylinder and update the velocity
			if (cos < 0) {
				float angle = 2 * normal.dot(ball.velocity);
				PVector temp = PVector.mult(normal, angle);
				//computation of the new velocity
				ball.velocity = PVector.sub(ball.velocity, temp);
				//lost of energy after hitting the cylinder
				ball.velocity = PVector.mult(ball.velocity, 0.5f);
			}
			return normal;

		} else {
			return new PVector(0, 0, 0);
		}
	}

	public void draw2D(PGraphics pGraphics) {
		pGraphics.stroke(fillColor);
		pGraphics.fill(fillColor);
		pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
	}

	public boolean isCylindric() {
		return true;
	}

	public String toString() {
		return "Cylinder with radius = " + radius ;
	}

	protected void updateObject() {
		super.updateObject();
	}

	protected void renderObject() {

		p.shape(cylinder);

	}


	protected void addPointsText(float velocity) {
		plateController.addPoints(PApplet.round(velocity), this);
		String text = "+"+ PApplet.round(velocity) * 100 +" kCal";
		PointsText pointsText = new PointsText(p, plateController, text, location.x, location.y - height , location.z);
		pointsText.fontSize = 70;
		pointsText.fontFillColor = p.color(0, 255, 0);
		pointsText.fontStrokeColor = p.color(132, 255, 128);
		plateController.addAnimatedTextPlate(pointsText);
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






}

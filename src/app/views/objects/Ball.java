package app.views.objects;

import app.controllers.PlateController;
import app.views.obstacles.PlateObstacleObject;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Ball extends PlateObject {

	public int fillColor;
	public float radius;

	Ball(PApplet parent, PlateController plateController, float radius, int fillColor) {
		super(parent, plateController);
		this.radius = radius;
		this.fillColor = fillColor;
		//shift in y-direction to place the ball on the plate
		location.y = PApplet.round(-plate.height/2.0f - radius);
	}

	public Ball(PApplet parent, PlateController plateController, float radius) {
		this(parent, plateController, radius, parent.color(255, 50, 10));
	}


	protected void updateObject() {
		super.updateObject();
		checkForCollisions();
	}

	protected void renderObject() {
		p.lights();
		p.noStroke();
		p.fill(fillColor);
		p.sphere(this.radius);
	}


	private void checkForCollisions() {
		PVector totalNormal = new PVector(0, 0, 0);
		for (PlateObstacleObject obstacle : plateController.obstacleList) {
			// check for collision
			PVector normalVector = obstacle.checkForCollisionWithBall(this);
			// add normal force of the current obstacle
			totalNormal.add(normalVector);
		}

		//apply the total normal force
		if (totalNormal.mag() > 0) {
			totalNormal.setMag(gravity.mag());
			applyForce(totalNormal);
		}
	}

	protected void drawAxes() {
		// X axe
		p.stroke(255, 0, 0);
		p.strokeWeight(2);
		p.line(0, 0, 0, radius * 2, 0, 0);
		p.line(0, 0, 0, -radius * 2, 0, 0);

		// Y axe
		p.stroke(0, 255, 0);
		p.strokeWeight(2);
		p.line(0, radius * 2, 0, 0, 0, 0);
		p.line(0, -radius * 2, 0, 0, 0, 0);

		// Z axe
		p.stroke(0, 0, 255);
		p.strokeWeight(2);
		p.line(0, 0, 0, 0, 0, radius * 2);
		p.line(0, 0, 0, 0, 0, -radius * 2);
	}

	public void draw2D(PGraphics pGraphics) {
		pGraphics.stroke(fillColor);
		pGraphics.fill(fillColor);
		pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
	}

	public String toString() {
		return "Ball with radius = " + radius;
	}
}

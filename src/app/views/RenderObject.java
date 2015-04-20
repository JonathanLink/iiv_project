package app.views;

import app.Parent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public abstract class RenderObject extends Parent {

	private static final float ZERO_THRESHOLD = 0.01f;

	public PVector location;
	public PVector velocity;
	public PVector acceleration;
	public PVector gravity;
	public float mass;
	public float angleX; 
	public float angleY; 
	public float angleZ;

	public RenderObject(PApplet parent) {
		super(parent);
		mass = 1.0f;
		angleX = 0.0f;
		angleY = 0.0f;
		angleZ = 0.0f;
		location = new PVector(0, 0, 0);
		velocity = new PVector(0, 0, 0);
		acceleration = new PVector(0, 0, 0);
		gravity = new PVector(0, 0, 0);
	}

	public abstract void draw2D(PGraphics pGraphics);

	public void draw2D() {
		this.draw2D(p.g);
	}

	public void render() {
		p.pushMatrix();
		p.rotateX(PApplet.radians(angleX));  
		p.rotateY(PApplet.radians(angleY));    
		p.rotateZ(PApplet.radians(angleZ));  
		p.translate(location.x, location.y, location.z);
		this.renderObject();
		p.popMatrix();
	}


	public void update() {  
		this.updateObject();
		velocity.add(acceleration);
		velocity = applyZeroThreshold(velocity);
		location.add(velocity);
		acceleration.mult(0);
	}

	public void applyForce(PVector force) {
		PVector dividedForce = PVector.div(force, mass);
		acceleration.add(dividedForce);
	}
	
	public PVector generateFrictionForce(float mu) {
		PVector friction = velocity.get();
		friction.mult(-1);
		friction.normalize();
		friction.mult(mu);
		return friction;
	}
	
	protected abstract void renderObject();
	protected abstract void updateObject();
	
	protected void drawAxes() {
		p.pushMatrix();
		// X axe
		p.stroke(255, 0, 0);
		p.strokeWeight(2);
		p.line(0, 0, 0, p.width, 0, 0);
		p.line(0, 0, 0, -p.width, 0, 0);
		p.textSize(20);
		p.fill(255, 0, 0);
		p.text("X", p.width/3, 0, 0); 

		// Y axe
		p.stroke(0, 255, 0);
		p.strokeWeight(2);
		p.line(0, p.width, 0, 0, 0, 0);
		p.line(0, -p.width, 0, 0, 0, 0);
		p.textSize(20);
		p.fill(0, 255, 0);
		p.text("Y", 0, p.width/3, 0); 

		// Z axe
		p.stroke(0, 0, 255);
		p.strokeWeight(2);
		p.line(0, 0, 0, 0, 0, p.width);
		p.line(0, 0, 0, 0, 0, -p.width); 
		p.textSize(20);
		p.fill(0, 0, 255);
		p.text("Z", 0, 0, p.width/3);
		p.popMatrix();
	}

	private PVector applyZeroThreshold(PVector v) {
		PVector tmpV = v.get();
		if (PApplet.abs(tmpV.x) < ZERO_THRESHOLD) {
			tmpV.x = 0.0f;
		}
		if (PApplet.abs(tmpV.y) < ZERO_THRESHOLD) {
			tmpV.y = 0.0f;
		}
		if (PApplet.abs(tmpV.z) < ZERO_THRESHOLD) {
			tmpV.z = 0.0f;
		}
		return tmpV;
	}


}

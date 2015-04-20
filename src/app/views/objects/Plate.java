package app.views.objects;

import app.controllers.MainController;
import app.views.RenderObject;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Plate extends RenderObject {

	static final int PLATE_WIDTH = 500;
	static final int PLATE_HEIGHT = 40;
	static final int PLATE_DEPTH = 500;

	public int fillColor;
	public float width;
	public float height;
	public float depth;
	public float speed;


	public Plate(PApplet parent) {
		this(parent,Plate.PLATE_WIDTH, Plate.PLATE_HEIGHT, Plate.PLATE_DEPTH);
	}

	public Plate(PApplet parent, float width, float height, float depth) {
		super(parent);
		this.width = width;
		this.height = height;
		this.depth = depth;
		speed = 1.0f;
		fillColor = p.color(192, 192, 192);
	}

	public void updateObject() {
	}

	public void renderObject() {
		p.stroke(140.0f, 140.0f, 140.0f);
		if (angleX > 0.0) {
			//transparent tilted plate to see the ball
			p.noFill();
		} else {
			p.fill(fillColor);
		}
		p.box(this.width, this.height, this.depth);
		if (MainController.DEBUG_MODE) drawAxes();
	}

	public void draw2D(PGraphics pGraphics) {
		pGraphics.stroke(fillColor);
		pGraphics.fill(fillColor);
		pGraphics.rect(0, 0, this.width, this.depth);
	}


	public String toString() {
		return "I' am the plate!";
	}
}

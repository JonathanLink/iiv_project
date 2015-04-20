package app.layers;

import app.Parent;
import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class Layer extends Parent {

	protected PGraphics layer;
	protected int width;
	protected int height;
	
	abstract protected void drawMyLayer();
	abstract protected int getX();
	abstract protected int getY();
	
	
	public Layer(PApplet parent) {
		this(parent, parent.displayWidth, 250);
	}

	public Layer(PApplet parent, int height) {
		this(parent, parent.displayWidth, height);
	}

	public Layer(PApplet parent, int width, int height) {
		super(parent);
		this.width = width;
		this.height = height;
		layer = p.createGraphics(this.width, this.height, PApplet.P3D);
	}

	public void draw() {
		p.pushMatrix();
		p.translate(-p.displayWidth/2.0f, -p.displayHeight/2.0f, 0);
		drawMyLayer();
		p.image(layer, getX(), getY());
		p.popMatrix();
	}


}

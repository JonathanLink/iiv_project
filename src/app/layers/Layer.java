package app.layers;

import app.Parent;
import app.controllers.MainController;
import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class Layer extends Parent {

	protected PGraphics layer;
	protected int width;
	protected int height;
	protected int posX;
	protected int posY;
	
	abstract protected void drawMyLayer();
		
	public Layer(PApplet parent, int width, int height, int posX, int posY) {
		super(parent);
		this.width = width;
		this.height = height;
		this.posX = posX;
		this.posY = posY;
		layer = p.createGraphics(this.width, this.height, PApplet.P2D);
	}

	public void draw() {
		p.image(layer, posX, posY);
		drawMyLayer();
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}

}

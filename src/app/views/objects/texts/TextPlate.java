package app.views.objects.texts;

import app.controllers.PlateController;
import app.views.objects.PlateObject;
import processing.core.PApplet;
import processing.core.PGraphics;

public class TextPlate extends PlateObject {

	protected String text;
	protected float fontSize;
	protected String fontPolice;
	protected int fontFillColor;
	protected int fontStrokeColor;
	protected float x;
	protected float y;
	protected float z;

	public TextPlate(PApplet parent, PlateController plateController, String text, float x, float y, float z) {
		super(parent, plateController); 
		this.text = text;
		this.fontSize = 20;
		this.fontPolice = "Lucida Sans";
		this.fontFillColor = p.color(0, 0, 0);
		this.fontStrokeColor = p.color(255, 255, 255);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void draw2D(PGraphics pGraphics) {
	}

	public String toString() {
		return "TextPlate (" + text + ")" ;
	}

	protected void renderObject() {
		p.textSize(fontSize);
		p.fill(fontFillColor);
		p.stroke(fontStrokeColor);
		p.text(text, x, y, z);
	}

	protected void updateObject() {
		super.updateObject();
	}

	protected void drawAxes() {
		
	}
}

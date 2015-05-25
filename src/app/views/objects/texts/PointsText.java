package app.views.objects.texts;

import app.controllers.PlateController;
import processing.core.PApplet;

public class PointsText extends AnimatedTextPlate {

	private static final float Y_SPEED = 1.0f;
	private static final float RELATIVE_MAX_Y = 100.0f;

	private float relativeMaxY = 0.0f;
	private float alphaFont = 255.0f;
	private float ySpeed = -1;

	public PointsText(PApplet parent, PlateController plateController, String text, float x, float y, float z) {
		super(parent, plateController, text, x, y, z);
		this.ySpeed = Y_SPEED;
	}
	
	public PointsText(PApplet parent, PlateController plateController, String text, float x, float y, float z, float fontSize, int fontColor, float ySpeed) {
		this(parent, plateController, text, x, y, z);
		this.fontSize = fontSize;
		this.fontFillColor = fontColor;
		this.ySpeed = ySpeed;
	}

	public boolean isAnimationFinished() {
		return (relativeMaxY >= RELATIVE_MAX_Y);
	}
	
	protected void renderObject() {
		p.textSize(fontSize);
		p.fill(fontFillColor);
		p.stroke(fontStrokeColor);
		p.text(text, x, y, z);
	}

	protected void updateObject() {
		super.updateObject();
		if (!isAnimationFinished()) {
			location.y = location.y - ySpeed;
			relativeMaxY = relativeMaxY + ySpeed;
			float red = fontFillColor >> 16 & 0xFF; // faster than fontFillColor.red() (optimization purpose)
			float green = fontFillColor >> 8 & 0xFF;
			float blue = fontFillColor >> 0xFF;
			alphaFont = (float) ((-255.0 / RELATIVE_MAX_Y) * relativeMaxY + 255.0);
			fontFillColor = p.color(red, green, blue, alphaFont);
		}
	}

}

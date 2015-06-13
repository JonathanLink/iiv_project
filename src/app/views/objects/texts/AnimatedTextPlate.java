package app.views.objects.texts;

import app.controllers.PlateController;
import processing.core.PApplet;

abstract public class AnimatedTextPlate extends TextPlate {
	
	public AnimatedTextPlate(PApplet parent, PlateController plateController, String text, float x, float y, float z) {
		super(parent, plateController, text, x, y, z);
	}
	
	public abstract boolean isAnimationFinished();
	
}
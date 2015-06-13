package app.views.objects.texts;

import processing.core.PApplet;
import app.controllers.PlateController;

public class StartCountdown extends AnimatedTextPlate{
	
	private boolean halfWayDone;
	
	public StartCountdown(PApplet parent, PlateController plateController,String text, float x, float y, float z) {
		super(parent, plateController, text, x, y, z);
		fontSize = 400;
		halfWayDone = false;
		fontFillColor = parent.color(255,0,0);
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
			z = z - 40;
		} 
	}
	
	@Override
	public boolean isAnimationFinished() {
		if (!halfWayDone && z < 0 && z > -1000) {
			halfWayDone = true;
			plateController.animatedTextHasFinishedHalfWay(this);
		}
		
		if (z <= -1500) {
			plateController.animatedTextHasFinished(this);
			return true;
		}
		
		return false;
	}
	

}

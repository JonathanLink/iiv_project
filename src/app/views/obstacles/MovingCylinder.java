package app.views.obstacles;

import processing.core.PApplet;
import app.controllers.PlateController;

public class MovingCylinder extends Cylinder {

	private boolean leftToRight;

	public MovingCylinder(PApplet parent, PlateController plateController) {
		super(parent, plateController);
		init();
	}

	public MovingCylinder(PApplet parent,PlateController plateController, float radius, float centerCoordX, float centerCoordZ) {
		super(parent, plateController, radius, centerCoordX, centerCoordZ, parent.color(255, 10, 25));
		init();
	}

	protected void updateObject() {
		super.updateObject();
		if (leftToRight) {
			if (location.x + 1 < plate.width/2.0 - radius) {
				location.x = location.x + 1;
				//location.y = 40.0 * sin(1.0/10.0 * frameCount) - 1 + 40; // décommente cette ligne si tu veux faire sautiller le cylindre ^^
			} else {
				leftToRight = false;
			}
		}
		if (!leftToRight) {
			if (location.x - 1 > -plate.width/2.0 + radius) {
				location.x = location.x - 1;
			} else {
				leftToRight = true;
			}
		}
	}

	private void init() {
		leftToRight = true;
	}


}

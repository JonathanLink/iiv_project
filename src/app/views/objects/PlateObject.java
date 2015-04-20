package app.views.objects;

import app.controllers.PlateController;
import app.views.RenderObject;
import processing.core.PApplet;

public abstract class PlateObject extends RenderObject {

	protected PlateController plateController;
	protected Plate plate;
	protected Ball ball;

	public PlateObject(PApplet parent, PlateController plateController) {
		super(parent);
		this.plateController = plateController;
		this.plate = plateController.plate;
		this.ball = plateController.ball;
	}


	protected void updateObject() {
		angleZ = plate.angleZ;
		angleX = plate.angleX;
	}
}

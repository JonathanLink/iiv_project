package app.layers;

import processing.core.PApplet;
import processing.core.PGraphics;
import app.controllers.PlateController;
import app.views.objects.Ball;
import app.views.obstacles.PlateObstacleObject;

public class DataVisualizationLayer extends Layer{

	private static final float TOP_VIEW_SCALE = 0.35f;
	private PlateController plateController;

	private PGraphics topView;
	private PGraphics scoreboard; // not yet used (céline à toi de jouer!)
	private PGraphics barChart;   // not yet used (céline à toi de jouer!)

	public DataVisualizationLayer(PApplet parent, PlateController plateController) {
		super(parent);
		this.plateController = plateController;
		this.topView = p.createGraphics(PApplet.round(plateController.plate.width * TOP_VIEW_SCALE), PApplet.round(plateController.plate.depth * TOP_VIEW_SCALE), PApplet.P2D);
	}

	protected void drawMyLayer() {
		
		drawTopView();
		drawScoreboard();
		drawBarChart();

		layer.beginDraw();
		layer.background(226, 223, 157);
		layer.image(topView, 15, 15); // 15px top/left margin for the topView 
		//layer.image(scoreboard, 0, 0);
		//layer.image(barChart, 0, 0);
		layer.endDraw();
		
	}

	protected int getX() {
		return 0;
	}

	protected int getY() {
		return p.displayHeight - super.height;
	}

	private void drawTopView() {
		topView.beginDraw();
		topView.background(255, 255, 255);
		topView.scale(TOP_VIEW_SCALE);
		plateController.plate.draw2D(topView);
		topView.translate(PApplet.round(plateController.plate.width/2.0f), PApplet.round(plateController.plate.depth/2.0f)); // initially put the ball at the center of the map
		Ball ball = plateController.ball;
		ball.draw2D(topView);
		for (PlateObstacleObject obstacle : plateController.obstacleList) {
			obstacle.draw2D(topView);
		}
		topView.endDraw();
	}
	
	private void drawScoreboard() {
		
	}
	
	private void drawBarChart() {
		
	}
	
	
	
}

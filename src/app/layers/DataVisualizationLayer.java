package app.layers;

import processing.core.PApplet;
import processing.core.PGraphics;
import app.controllers.PlateController;
import app.views.objects.Ball;
import app.views.obstacles.PlateObstacleObject;

public class DataVisualizationLayer extends Layer{

	private static final float TOP_VIEW_SCALE = 0.28f;
	private static final int MARGIN_BTW_COMPONENT = 20;
	private static final int Y_ALIGN = 10;
	private static final int COMPONENT_HEIGHT = 160;
	private static final int TIME_STEP = 2000; //2 sec

	private PlateController plateController;

	private PGraphics topView;
	private PGraphics scoreBoard; 
	private PGraphics barChart; 
	private PGraphics scrollbar; 

	//Variables for barChart
	private int[] chart; //array that stores the stat at each step
	private int indexTime = -1;
	private int timer = p.millis();

	//Variables for scrollbar
	private float sliderPosition;
	private float newSliderPosition;
	private float sliderPositionMin, sliderPositionMax;

	public DataVisualizationLayer(PApplet parent, PlateController plateController) {
		super(parent, parent.width,180,0,parent.height-180);
		this.plateController = plateController;

		//dimension of each component
		this.topView = p.createGraphics(PApplet.round(plateController.plate.width * TOP_VIEW_SCALE), PApplet.round(plateController.plate.depth * TOP_VIEW_SCALE), PApplet.P2D);
		this.scoreBoard = p.createGraphics(125, COMPONENT_HEIGHT, PApplet.P2D);
		this.barChart = p.createGraphics(915,130,PApplet.P2D);
		this.scrollbar = p.createGraphics(300,15, PApplet.P2D);
		sliderPosition = scrollbar.width/2 - scrollbar.height/2;
		newSliderPosition = sliderPosition;
		sliderPositionMin = 0;
		sliderPositionMax = scrollbar.width - scrollbar.height;
		chart = new int [(barChart.width/4)]; //4 is the minimum width of the tiny rectangles
	}

	public boolean isMouseOverSlider() {
		int xPosition= topView.width + scoreBoard.width + 3*MARGIN_BTW_COMPONENT - this.getX();
		int yPosition =Y_ALIGN + barChart.height + 10 + this.getY();
		return (p.mouseX > xPosition && p.mouseX < xPosition+scrollbar.width &&
				p.mouseY > yPosition -10);
	}

	//Return the clicked value in the scrollbar coordinates, i.e zero is on the left of the component
	public int getCoordXScrollbar() {
		return p.mouseX - (topView.width + scoreBoard.width + 3*MARGIN_BTW_COMPONENT - this.getX());
	}


	//The slider position in the interval [0,1]
	public float getSliderPos() {
		return (sliderPosition)/(scrollbar.width - scrollbar.height);
	}

	//Clamps the value into the interval
	float constrain(float val, float minVal, float maxVal) { 
		return PApplet.min(PApplet.max(val, minVal), maxVal);
	}
	
	protected void drawMyLayer() {

		drawTopView();
		drawScoreboard();
		drawBarChart();
		drawScrollbar();

		layer.beginDraw();
		layer.background(204, 255, 255);	

		//Positions of the graphic components relative to the principal layer
		layer.image(topView, MARGIN_BTW_COMPONENT, Y_ALIGN);
		layer.image(scoreBoard, topView.width + 2*MARGIN_BTW_COMPONENT, Y_ALIGN);
		layer.image(barChart, topView.width + scoreBoard.width + 3*MARGIN_BTW_COMPONENT, Y_ALIGN);
		layer.image(scrollbar,topView.width + scoreBoard.width + 3*MARGIN_BTW_COMPONENT, Y_ALIGN + barChart.height + 10);

		layer.endDraw();

	}

	private void drawTopView() {
		topView.beginDraw();
		topView.scale(TOP_VIEW_SCALE);

		//Draw the plate in 2d
		plateController.plate.draw2D(topView);
		topView.translate(PApplet.round(plateController.plate.width/2.0f), PApplet.round(plateController.plate.depth/2.0f)); //set the center of the 2D plate at origin

		//Draw the ball in 2d
		Ball ball = plateController.ball;
		ball.draw2D(topView);

		//Draw the obstacles in 2d
		for (PlateObstacleObject obstacle : plateController.obstacleList) {
			obstacle.draw2D(topView);
		}
		topView.endDraw();
	}

	private void drawScoreboard() {
		scoreBoard.beginDraw();
		scoreBoard.background(255, 255, 204);

		//Frame of the scoreboard
		scoreBoard.noFill();
		scoreBoard.stroke(plateController.plate.fillColor);
		scoreBoard.rect(0, 0, scoreBoard.width-1, scoreBoard.height-1);

		//Text of the scoreboard
		scoreBoard.fill(0);  
		scoreBoard.textSize(12);
		scoreBoard.text("Total Score", 15, 20);
		scoreBoard.text("Velocity", 15, 80);
		scoreBoard.text("Last Score", 15, 120);
		//TODO what does last score represent?
		//scoreBoard.text("-- pts", 15, 135);

		if (plateController.totalScore < 0) {
			scoreBoard.fill(255, 0, 0);
			scoreBoard.text("--", 15, 35);
		} else {
			scoreBoard.fill(0);
			scoreBoard.text(plateController.totalScore, 15, 35);
		}

		if (plateController.gainPoints < 0) {
			scoreBoard.fill(255, 0, 0);
			scoreBoard.text("(" + plateController.gainPoints +")", 15, 50);
		} else if (plateController.gainPoints > 0) {
			scoreBoard.fill(0, 255, 0);
			scoreBoard.text("(+" + plateController.gainPoints + ")", 15, 50);
		} 

		scoreBoard.fill(0); 
		scoreBoard.text(plateController.ball.velocity.mag(), 15, 95);

		scoreBoard.endDraw();

	}

	private void drawBarChart() {
		barChart.beginDraw();
		barChart.background(255, 255, 204);

		//Frame of the chart
		barChart.noFill();
		barChart.stroke(plateController.plate.fillColor);
		barChart.rect(0, 0, barChart.width-1, barChart.height-1);

		//Initialize variables
		int tinyRectHeight = 8;
		float tinyRectWidth = (getSliderPos()+0.5f)*tinyRectHeight;
		int numberOfTinyRect = PApplet.floor(plateController.totalScore);
		if (numberOfTinyRect > (barChart.height-10)/tinyRectHeight) {
			numberOfTinyRect = (barChart.height-10)/tinyRectHeight;
		} 

		//Use millis() and a timer to update the chart every 2 seconds
		if (p.millis() - timer >= TIME_STEP) {
			indexTime += 1;
			if (indexTime < chart.length) {
				chart[indexTime] = numberOfTinyRect;
			} 
			timer = p.millis();
		}

		//Redraw the stats according to the time and the score every 2 seconds

		for (int i = 0; i < indexTime  && indexTime < barChart.width/tinyRectWidth ; i++) {
			int score = chart[i];
			int time = i;
			if (score <= 0) {
				barChart.fill(204, 255, 255);
				barChart.rect(time*tinyRectWidth, barChart.height - 3, tinyRectWidth, 2);
			} else {
				for (int j = 1; j <= score; j++) {      
					barChart.fill(204-(j*20), 255, 299-(j*10));
					barChart.rect(time*tinyRectWidth, barChart.height - (tinyRectHeight*j)-2, tinyRectWidth, tinyRectHeight);
				}
			}
		} 
		barChart.endDraw();
	}

	private void drawScrollbar() {
		scrollbar.beginDraw();
		scrollbar.background(255, 255, 204);

		//Frame of the scrollbar
		scrollbar.fill(255, 255, 204);
		scrollbar.stroke(plateController.plate.fillColor);
		scrollbar.rect(0, 0, scrollbar.width-1, scrollbar.height-1);

		//Slider of the scrollbar
		if (isMouseOverSlider()) {
			scrollbar.fill(0, 0, 0);
		} else {
			scrollbar.fill(102, 102, 102);
		}
		scrollbar.noStroke();

		if (isMouseOverSlider() && p.mousePressed) {
			plateController.locked = true; //plate dragged is locked
			newSliderPosition = constrain((float) (getCoordXScrollbar()  - scrollbar.height/2.0), sliderPositionMin, sliderPositionMax);
		} else {
			plateController.locked = false;
		}

		if (PApplet.abs(newSliderPosition - sliderPosition) > 1) {
			sliderPosition = sliderPosition + (newSliderPosition - sliderPosition);
		} 

		scrollbar.rect(sliderPosition, 0, scrollbar.height, scrollbar.height);
		scrollbar.endDraw();
	}




}




class DataVisualizationLayer extends Layer {

  private static final float TOP_VIEW_SCALE = 0.35;
  private PlateController plateController;
  private ArrayList<BallHistory> ballHistory;

  private PGraphics topView;
  private PGraphics scoreboard; // not yet used
  private PGraphics barChart;   // not yet used

  DataVisualizationLayer(PlateController plateController) {
    this.plateController = plateController;
    this.ballHistory = new ArrayList<BallHistory>();
    this.topView = createGraphics(round(plateController.plate.width * TOP_VIEW_SCALE), round(plateController.plate.depth * TOP_VIEW_SCALE), P2D);
  }

  void drawMyLayer() {

    drawTopView();
    //drawScoreboard();
    //drawBarChart();

    layer.beginDraw();
    layer.background(226, 223, 157);

    layer.image(topView, 15, 15);
    //layer.image(scoreboard, 0, 0);
    //layer.image(barChart, 0, 0);


    layer.endDraw();
  }

  int getX() {
    return 0;
  }

  int getY() {
    return displayHeight - super.height;
  }


  void drawTopView() {
    topView.beginDraw();
    topView.background(255, 255, 255);
    topView.scale(TOP_VIEW_SCALE);
    plateController.plate.draw2D(topView);
    topView.translate(round(plateController.plate.width/2.0), round(plateController.plate.depth/2.0)); // initially put the ball at the center of the map
    //drawBallHistory();
    Ball ball = plateController.ball;
    ball.draw2D(topView);
    //addBallInHistory(ball);
    for (PlateObstacleObject obstacle : plateController.obstacleList) {
      obstacle.draw2D(topView);
    }
    topView.endDraw();
  }

  void addBallInHistory(Ball ball) {
    // add ball in history only each 1/10 sec (optimizazion purpose)
    if ( (int)frameCount % (int)frameRate/10 == 0) {
      ballHistory.add(new BallHistory(ball));
    }
  }

  void drawBallHistory() {
    for (BallHistory ballHistory : this.ballHistory) {
      ballHistory.draw(topView);
    }
  }

  void drawScoreboard() {
  }
}

class BallHistory {
  PVector location;
  color fillColor; 
  float radius;
  BallHistory(Ball ball) {
    this.location = ball.location.get(); 
    this.radius = ball.radius;
    float red = ball.fillColor >> 16 & 0xFF; // faster than ball.fillColor.red() (optimizazion purpose)
    float green = ball.fillColor >> 8 & 0xFF;
    float blue = ball.fillColor >> 0xFF;
    this.fillColor = color(red, green, blue, 10);
  }

  void draw(PGraphics pGraphics) {
    pGraphics.stroke(fillColor);
    pGraphics.fill(fillColor);
    pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
  }
}


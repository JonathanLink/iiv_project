public class Ball extends PlateObject {

  static final float BALL_RADIUS = 15.0;


  public color fillColor;
  private float radius;

  Ball(PlateController plateController, float radius, color fillColor) {
    super(plateController);
    this.radius = radius;
    this.fillColor = fillColor;
    //shift in y-direction to place the ball on the plate
    location.y = round(-plate.height/2.0 - radius);
  }

  Ball(PlateController plateController) {
    this(plateController, Ball.BALL_RADIUS, color(255, 50, 10));
  }


  void updateObject() {
    super.updateObject();
    checkForCollisions();
  }

  void renderObject() {
    lights();
    noStroke();
    fill(fillColor);
    sphere(this.radius);
  }


  private void checkForCollisions() {
    PVector totalNormal = new PVector(0, 0, 0);
    for (PlateObstacleObject obstacle : plateController.obstacleList) {
      // check for collision
      PVector normalVector = obstacle.checkForCollisionWithBall(this);
      // add normal force of the current obstacle
      totalNormal.add(normalVector);
    }

    //apply the total normal force
    if (totalNormal.mag() > 0) {
      totalNormal.setMag(gravity.mag());
      applyForce(totalNormal);
    }
  }

  void drawAxes() {
    // X axe
    stroke(255, 0, 0);
    strokeWeight(2);
    line(0, 0, 0, radius * 2, 0, 0);
    line(0, 0, 0, -radius * 2, 0, 0);

    // Y axe
    stroke(0, 255, 0);
    strokeWeight(2);
    line(0, radius * 2, 0, 0, 0, 0);
    line(0, -radius * 2, 0, 0, 0, 0);

    // Z axe
    stroke(0, 0, 255);
    strokeWeight(2);
    line(0, 0, 0, 0, 0, radius * 2);
    line(0, 0, 0, 0, 0, -radius * 2);
  }

  void draw2D(PGraphics pGraphics) {
    pGraphics.stroke(fillColor);
    pGraphics.fill(fillColor);
    pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
  }

  String toString() {
    return "Ball with radius = " + radius;
  }
}


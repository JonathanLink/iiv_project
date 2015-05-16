class PlateEdges extends PlateObstacleObject {

  PlateEdges(PlateController plateController) {
    super(plateController);
  }

  void renderObject() {
  }

  PVector checkForCollisionWithBall(Ball ball) {
    
    PVector normal = new PVector(0, 0, 0);
    
    if (ball.location.x  > plate.width/2.0 - ball.radius) {
      ball.location.x = plate.width/2.0 - ball.radius;
      ball.velocity.x *= -0.5;
      normal.x = ball.velocity.x;
    } else if (ball.location.x < -plate.width/2.0 + ball.radius) {
      ball.location.x = -plate.width/2.0 + ball.radius;
      ball.velocity.x *= -0.5;
      normal.x = ball.velocity.x;
    }

    if (ball.location.z  > plate.depth/2.0 - ball.radius) {
      ball.location.z = plate.depth/2.0 - ball.radius;
      ball.velocity.z *= -0.5;
      normal.z = ball.velocity.z;
    } else if (ball.location.z < -plate.depth/2.0 + ball.radius) {
      ball.location.z = -plate.depth/2.0 + ball.radius;
      ball.velocity.z *= -0.5;
      normal.z = ball.velocity.z;
    }

    return normal;
  }

  void draw2D(PGraphics pGraphics) {
    //stroke(color(255,0,0));
    //rect(0, 0, plate.width, plate.height);
  }
}


class EditController extends Controller {

  private Plate plate;
  private Ball ball;
  private PlateController plateController;
  
  static final int STATIC_CYLINDER = 0;
  static final int MOVING_CYLINDER = 1;
  int currentObstacle;
  
  EditController(PlateController plateController) {
    this.plate = plateController.plate;
    this.ball = plateController.ball;
    this.plateController = plateController;
    currentObstacle = STATIC_CYLINDER;
  }

  void draw() {
    setOrigin(displayWidth/2.0, displayHeight/2.0, 0); 

    //draw the plate in 2D
    pushMatrix();
    translate(-plate.width/2.0, -plate.depth/2.0);
    plate.draw2D();
    popMatrix();

    //draw the ball in 2D
    ball.draw2D();

    //draw all obstacles in 2D
    for (PlateObstacleObject obstacle : plateController.obstacleList) {
      obstacle.draw2D();
    }

    //test if the mouse click is on the plate to display the cursor
    if (isMouseInsidePlate()) {
      cursor(CROSS);
    }
  }

  boolean isMouseInsidePlate() {
    return (getCoordX() < plate.width/2 &&
      getCoordX() > -plate.width/2 &&
      getCoordY() > -plate.depth/2 &&
      getCoordY() < plate.depth/2);
  }

  boolean isBallClear(PVector mouseLocation) {
    PVector distanceToTheBall = PVector.sub(mouseLocation, ball.location); 
    distanceToTheBall.y = 0; 
    if (distanceToTheBall.mag() < Cylinder.CYLINDER_RADIUS + ball.radius) { // TODO: optimiser, on aura pas forcément que des formes cylindres (rectangle par ex.)
      consoleLayer.write("ERROR: you cannot add a cylinder here,it would overlap with the ball");
      return false;
    }
    return true;
  }

  boolean isObstacleClear(PVector mouseLocation) {
    for (PlateObstacleObject obstacle : plateController.obstacleList) {
      PVector distanceBetweenCylinders = PVector.sub(mouseLocation, obstacle.location);      
      if (distanceBetweenCylinders.mag() < 2.0*Cylinder.CYLINDER_RADIUS ) {
        consoleLayer.write("ERROR: you cannot add a cylinder here,it would overlap another cylinder");
        return false;
      }
    }
    return true;
  }

  void mouseClicked() {
    //test if the mouse click is on the plate
    if (isMouseInsidePlate()) {
      PVector mouseLocation = new PVector(getCoordX(), 0, getCoordY());

      //Prevent to add an obstacle on the ball
      if (!isBallClear(mouseLocation)) return;
      //Prevent to add an obstacle which could overlapp another obstacle
      if (!isObstacleClear(mouseLocation)) return;

      //Create a cylinder with the clicked location as center
      Cylinder cylinder = (currentObstacle == STATIC_CYLINDER)? new Cylinder(plateController, Cylinder.CYLINDER_RADIUS, mouseLocation.x, mouseLocation.z) : new MovingCylinder(plateController, Cylinder.CYLINDER_RADIUS, mouseLocation.x, mouseLocation.z);
      plateController.addPlateObstacle(cylinder);
    }
  }

  void keyPressed() {
    if (key == CODED && keyCode == UP) {
      currentObstacle = STATIC_CYLINDER;
    } else if (key == CODED && keyCode == DOWN) {
      currentObstacle = MOVING_CYLINDER;
    }
  }
}


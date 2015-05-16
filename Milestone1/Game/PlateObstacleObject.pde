abstract class PlateObstacleObject extends PlateObject {
  PlateObstacleObject(PlateController plateController) {
    super(plateController);
  }
  
  abstract PVector checkForCollisionWithBall(Ball ball);
}

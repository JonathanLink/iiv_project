abstract class PlateObject extends RenderObject {

  protected PlateController plateController;
  protected Plate plate;
  protected Ball ball;

  PlateObject(PlateController plateController) {
    this.plateController = plateController;
    this.plate = plateController.plate;
    this.ball = plateController.ball;
  }
  

  void updateObject() {
    angleZ = plate.angleZ;
    angleX = plate.angleX;
  }
}


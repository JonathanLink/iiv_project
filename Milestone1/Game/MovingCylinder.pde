class MovingCylinder extends Cylinder {

  boolean leftToRight;

  MovingCylinder(PlateController plateController) {
    super(plateController);
    init();
  }

  MovingCylinder(PlateController plateController, float radius, float centerCoordX, float centerCoordZ) {
    super(plateController, radius, centerCoordX, centerCoordZ, color(255, 10, 25));
    init();
  }
  
  void init() {
     leftToRight = true;
  }

  void updateObject() {
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
}


import java.util.Iterator;
class PlateController extends Controller {
  
  static final public float GRAVITY_CONST = 0.05; 
  static final public float LIMIT_ANGLE = 60.0; //angle in degree
  static final public float SPEED_MIN = 0.5;
  static final public float SPEED_MAX = 1.5;
  static final public float SPEED_STEP = 0.05;
  static final public float FRICTION_COEF = 0.001;

  Plate plate;
  Ball ball;
  ArrayList<PlateObstacleObject> obstacleList;
  ArrayList<AnimatedTextPlate> animatedTextList;
  //ArrayList<RenderObject> othersList;

  PlateController() {
    this.obstacleList = new ArrayList<PlateObstacleObject>();
    this.animatedTextList = new ArrayList<AnimatedTextPlate>();
    this.plate = new Plate();
    this.ball = new Ball(this);
    // add plate edges (4 walls)
    PlateEdges plateEdges = new PlateEdges(this);
    this.addPlateObstacle(plateEdges);
  }

  void addPlateObstacle(PlateObstacleObject obstacle) {
    obstacleList.add(obstacle);
  }

  void addAnimatedTextPlate(AnimatedTextPlate animatedTextPlate) {
    animatedTextList.add(animatedTextPlate);
  }

  void draw() {
    setOrigin(displayWidth/2.0, displayHeight/2.0, 0); //the origin of the plate is at the middle point of the window
    updateAllObjects();
    renderAllObjects();
  }

  void updateAllObjects() {
    plate.update();
    for (PlateObstacleObject obstacle : obstacleList) {
      obstacle.update();
    }
    addGravity(ball);
    addFrictionForce(ball);
    ball.update();
    updateAnimatedTextPlate();
  }

  void updateAnimatedTextPlate() {
    Iterator<AnimatedTextPlate> iterator = animatedTextList.iterator();
    while (iterator.hasNext ()) {
      AnimatedTextPlate animatedTextPlate = iterator.next();
      animatedTextPlate.update();
      if (animatedTextPlate.isAnimationFinished()) {
        iterator.remove();
      }
    }
  }

  void renderAllObjects() {
    plate.render();
    for (PlateObstacleObject obstacle : obstacleList) {
      obstacle.render();
    }
    ball.render();
    for (AnimatedTextPlate animatedTextPlate : animatedTextList) {
      animatedTextPlate.render();
    }
  }

  private void addFrictionForce(PlateObject plateObject) {
    PVector frictionForce = plateObject.generateFrictionForce(FRICTION_COEF);
    plateObject.applyForce(frictionForce);
  }

  private void addGravity(PlateObject plateObject) {
    plateObject.gravity.x = sin(radians(plate.angleZ)) * GRAVITY_CONST;
    plateObject.gravity.z = -sin(radians(plate.angleX)) * GRAVITY_CONST;
    plateObject.applyForce(plateObject.gravity);
  }


  void mouseDragged() {  
    float x = (mouseX - pmouseX) * (LIMIT_ANGLE / ((plate.depth * 1.2) / 2.0) );
    float y = (mouseY - pmouseY) * (LIMIT_ANGLE / ((plate.width * 1.2) / 2.0) );    
    if (mouseX <= width && mouseY <= height) {
      if ((plate.angleZ + x * plate.speed) <= LIMIT_ANGLE && (plate.angleZ + x * plate.speed) >= -LIMIT_ANGLE) {
        plate.angleZ += x * plate.speed;
      } 

      if ((plate.angleX - y * plate.speed ) <= LIMIT_ANGLE && (plate.angleX - y * plate.speed) >= -LIMIT_ANGLE) {
        plate.angleX -= y * plate.speed;
      }
    }
  }  

  void mouseWheel(MouseEvent event) {
    float count = event.getCount();
    if (count > 0 && (plate.speed + SPEED_STEP) <= SPEED_MAX) {
      plate.speed += SPEED_STEP;
    } else if (count < 0 && (plate.speed - SPEED_STEP) >= SPEED_MIN) {
      plate.speed -= SPEED_STEP;
    }
  }
}


public class Plate extends RenderObject {

  final private float LIMIT_ANGLE = 60.0; // deg
  final private float SPEED_MIN = 0.5;
  final private float SPEED_MAX = 1.5;
  final private float SPEED_STEP = 0.1;

  float w;
  float h;
  float d;

  private float speed = 1.0;
  
  private ArrayList<Obstacle> obstacles;

  Plate(float w, float h, float d) {
    this.w = w;
    this.h = h;
    this.d = d;
    obstacles = new ArrayList<Obstacle>();
  }
  
  void addObstacle(Obstacle obstacle) {
    obstacles.add(obstacle);
  }

  void updateObject() {
  }

  void renderObject() {
    stroke(0.0, 0.0, 0.0);
    if (angleX > 0.0) {
      noFill();
    } else {
      fill(255.0, 255.0, 255.0);
    }
    box(this.w, this.h, this.d);
  }



  void mouseDragged() {  
    float x = (mouseX - pmouseX) * (LIMIT_ANGLE / ((d * 1.2) / 2.0) );
    float y = (mouseY - pmouseY) * (LIMIT_ANGLE / ((w * 1.2) / 2.0) );    
    if (mouseX <= width && mouseY <= height) {
      if ((super.angleZ + x * speed) <= LIMIT_ANGLE && (angleZ + x * speed) >= -LIMIT_ANGLE) {
        super.angleZ += x * speed;
      } 

      if ((super.angleX - y * speed ) <= LIMIT_ANGLE && (super.angleX - y * speed) >= -LIMIT_ANGLE) {
        super.angleX -= y * speed;
      }
    }
  }  

  void mouseWheel(MouseEvent event) {
    float count = event.getCount();
    if (count > 0 && (speed + SPEED_STEP) <= SPEED_MAX) {
      speed += SPEED_STEP;
    } else if (count < 0 && (speed - SPEED_STEP) >= SPEED_MIN) {
      speed -= SPEED_STEP;
    }
  }


  String toString() {
    return "I' am the plate!";
  }
}


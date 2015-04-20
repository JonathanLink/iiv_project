public class Plate extends RenderObject {

  final private float LIMIT_ANGLE = 60.0; //angle in degree
  final private float SPEED_MIN = 0.5;
  final private float SPEED_MAX = 1.5;
  final private float SPEED_STEP = 0.05;
  final private color c;
   
  float w;
  float h;
  float d;
  float speed;

  Plate(float w, float h, float d) {
    this.w = w;
    this.h = h;
    this.d = d;
    speed = 1.0;
    c = color(192, 192, 192);
    
  }
  
  void updateObject() {
    //the plate is updated by user interaction using keyboard and mouse
     if (DEBUG_MODE) speedOfTilt.updateText("Speed of tilt: " + nf(speed,1,2));
  }

  void renderObject() {
    stroke(140.0, 140.0, 140.0);
    if (angleX > 0.0) {
      //transparent tilted plate to see the ball
      noFill();
    } else {
      fill(c);
    }
    box(this.w, this.h, this.d);
    if (DEBUG_MODE) drawAxes(); 
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


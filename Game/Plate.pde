public class Plate extends RenderObject {

  final private float LIMIT_ANGLE = 60.0;
  final private float SPEED_MIN = 0.5;
  final private float SPEED_MAX = 1.5;
  final private float SPEED_STEP = 0.1;
  
  float w;
  float h;
  float d;

  float speed = 1.0;

  Plate(float w, float h, float d) {
    this.w = w;
    this.h = h;
    this.d = d;
  }

  void renderObject() {
    stroke(0.0, 0.0, 0.0);
    fill(255.0, 255.0, 255.0);
    box(this.w, this.h, this.d);
  }


  void mouseDragged() {  
    float x = (mouseX - pmouseX)*60.0/(width/2.0);
    float y = (mouseY - pmouseY)*60.0/(height/2.0);    
    //x = map(x, 0, 60, 0.1, 60);
    
     
     if (mouseX <= width && mouseY <= height) {
       
      println("x= " + x + " y= " +y);
         
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

     if (count > 0 && (speed+SPEED_STEP) <= SPEED_MAX) {
         speed += SPEED_STEP;  
      } else if (count < 0 && (speed - SPEED_STEP) >= SPEED_MIN) {
         speed -= SPEED_STEP;
      }
      
      println("speed=" + speed);

  }
  
}


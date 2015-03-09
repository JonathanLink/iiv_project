public class Plate extends RenderObject  {
  
  float w;
  float h;
  float d;
  
  float angleX = 0;
  float angleY = 0; 
  float angleZ = 0; 
  int alpha = 1;
  
  
  Plate(float w, float h, float d) {
    this.w = w;
    this.h = h;
    this.d = d; 
  }
  
  void render() {
    pushMatrix();
    rotateX(radians(-this.angleX));
    rotateY(radians(this.angleY));
    rotateZ(radians(this.angleZ));
    stroke(0.0, 0.0, 0.0);
    box(this.w, this.h , this.d);
    popMatrix();  
  }
  
  void keyPressed() {
    if (key == CODED) {
      switch(keyCode) {
        case LEFT:
          angleY += -alpha*3;  
          break;
          
        case RIGHT:
          angleY += alpha*3; 
          break; 
       }
    }
  }
  
  
 void mouseDragged(MouseEvent event) {  
  float x = event.getX() - width/2;
  float y = event.getY() - height/2;
  float rotateZpercent = abs(x/(width/2));
  float rotateXpercent = abs(y/(height/2));
  float angleZ_updated = rotateZpercent*30;
  float angleX_updated = rotateXpercent*30;
  //println("angleZ_updated " + angleZ_updated + " angleX_udpated " + angleX_updated);

 //define the four cadrants
   if (x>0 && y>0) {
       angleX = angleX_updated;
       angleZ = angleZ_updated;
   } else if (x<0 && y>0) {
      angleX = angleX_updated;
      angleZ = -angleZ_updated;  
   } else if (x<0 && y<0) {
     angleX = -angleX_updated;
     angleZ = -angleZ_updated; 
   } else if (x>0 && y<0) {
     angleX = -angleX_updated;
     angleZ = angleZ_updated;
   }
}
  
  void mouseWheel(MouseEvent event) {
    //getCount is a number between -10 and 10
    //if it is positive, we want the angle to increase
    //if it is negative, we want the angle to decrease
    println("alpha " + alpha + "/ getCount() " + event.getCount());
    alpha = abs(event.getCount());
  }

  
  
}

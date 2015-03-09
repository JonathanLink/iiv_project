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
    rotateX(this.angleX);
    rotateY(this.angleY);
    rotateZ(this.angleZ);
    stroke(0.0, 0.0, 0.0);
    box(this.w, this.h , this.d);
    popMatrix();  
  }
  
  void keyPressed() {
    if (key == CODED) {
      switch(keyCode) {
        case LEFT:
          angleY += -alpha*PI/180;  
          break;
          
        case RIGHT:
          angleY += alpha*PI/180; 
          break; 
       }
    }
  }
  
 void mouseDragged(MouseEvent event) {  
  if (event.getX() > 2*width/3 && event.getY() > height/3 && event.getY() < 2*height/3) {
    if(angleZ < PI/3){
      angleZ += alpha*PI/180;
    }
  } else if (event.getX() < width/3 && event.getY() > height/3 && event.getY() < 2*height/3) {
    if(angleZ > -PI/3){
      angleZ -= alpha*PI/180;
    }
  } else if(event.getX() > width/3 && event.getX() < 2*width/3 && event.getY() > 2*height/3) {
    if(angleX > -PI/3){
      angleX -= alpha*PI/180;
    }
  
  } else if(event.getX() > width/3 && event.getX() < 2*width/3 && event.getY() < height/3) {
    if(angleX < PI/3){
      angleX += alpha*PI/180;
    }
  }  else if(event.getX() < width/3 && event.getY() < height/3) {
    if(angleZ > -PI/3 && angleX < PI/3){
      angleX += alpha*PI/180;
      angleZ -= alpha*PI/180;
    }
  } else if(event.getX() > 2*width/3 && event.getY() < height/3) {
    if(angleZ < PI/3 && angleX > -PI/3){
      angleX += alpha*PI/180;
      angleZ += alpha*PI/180;
    }
  } else if(event.getX() > 2*width/3 && event.getY() > 2*height/3) {
    if(angleZ < PI/3 && angleX < PI/3){
      angleX -= alpha*PI/180;
      angleZ += alpha*PI/180;
    }
  } else if(event.getX() < width/3 && event.getY() > 2*height/3) {
    if(angleZ > -PI/3 && angleX < PI/3){
      angleX -= alpha*PI/180;
      angleZ -= alpha*PI/180;
    }
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

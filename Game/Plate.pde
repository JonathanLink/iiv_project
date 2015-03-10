public class Plate extends RenderObject  {
  
  final private float LIMIT_ANGLE = PI / 6.0;
    
  float w;
  float h;
  float d;
  
  int alpha = 1;
  
  
  Plate(Game game, float w, float h, float d) {
    super(game);
    this.w = w;
    this.h = h;
    this.d = d; 
  }
  
  void renderObject() {
    stroke(0.0, 0.0, 0.0);
    fill(255.0, 255.0, 255.0);
    box(this.w, this.h , this.d);
  }
  
  void keyPressed() {
    if (key == CODED) {
      switch(keyCode) {
        case LEFT:
          super.angleY += -alpha*3;  
          break;
          
        case RIGHT:
          super.angleY += alpha*3; 
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
     super.angleX = angleX_updated;
     super.angleZ = angleZ_updated;
   } else if (x<0 && y>0) {
     super.angleX = angleX_updated;
     super.angleZ = -angleZ_updated;  
   } else if (x<0 && y<0) {
     super.angleX = -angleX_updated;
     super.angleZ = -angleZ_updated; 
   } else if (x>0 && y<0) {
     super.angleX = -angleX_updated;
     super.angleZ = angleZ_updated;
   }
   
   super.angleX = radians(angleX * -1);
   super.angleY = radians(angleY);
   super.angleZ = radians(angleZ);
   
}
  


  
  
}

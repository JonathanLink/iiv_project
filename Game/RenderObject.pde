abstract class RenderObject {
  
  protected float angleX = 0.0;
  protected float angleY = 0.0;
  protected float angleZ = 0.0;
  protected float translateX = 0.0;
  protected float translateY = 0.0;
  protected float translateZ = 0.0;
  protected float scaleX = 1.0;
  protected float scaleY = 1.0;
  protected float scaleZ = 1.0;
  protected float mouseWheelCount = 0.0;
  
  RenderObject() {
    
  }
  
  abstract void renderObject();
  
  void render() {
    pushMatrix(); 
    rotateX(radians(this.angleX));  
    rotateY(radians(this.angleY));    
    rotateZ(radians(this.angleZ));
    if (DEBUG_MODE) drawAxes();
    this.renderObject();
    popMatrix();
  }

  void keyPressed() {
  };
  
  void mouseDragged() {
  }; 
  
  void mouseWheel(MouseEvent event) {
  };

  protected void drawAxes() {

    // X axe
    stroke(255, 0, 0);
    strokeWeight(2);
    line(0, 0, 0, width, 0, 0);
    line(0, 0, 0, -width, 0, 0);
    textSize(20);
    fill(255, 0, 0);
    text("X", width/3, 0, 0); 

    // Y axe
    stroke(0, 255, 0);
    strokeWeight(2);
    line(0, width, 0, 0, 0, 0);
    line(0, -width, 0, 0, 0, 0);
    textSize(20);
    fill(0, 255, 0);
    text("Y", 0, width/3, 0); 

    // Z axe
    stroke(0, 0, 255);
    strokeWeight(2);
    line(0, 0, 0, 0, 0, width);
    line(0, 0, 0, 0, 0, -width); 
    textSize(20);
    fill(0, 0, 255);
    text("Z", 0, 0, width/3);
    
  }
  
  
}


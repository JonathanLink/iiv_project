abstract class RenderObject {

  public static final float GRAVITY = 0.1; 

  
  public PVector location;
  protected PVector velocity;
  protected PVector acceleration;
  protected PVector gravity;
  protected float mass;
  
  protected float angleX = 0.0;
  protected float angleY = 0.0;
  protected float angleZ = 0.0;
  protected float mouseWheelCount = 0.0;
  
  protected float scale;
  
  
  RenderObject() {
    mass = 1.0;
    location = new PVector(0, 0, 0);
    velocity = new PVector(0, 0, 0);
    acceleration = new PVector(0, 0, 0);
    gravity = new PVector(0, 0, 0);
    scale = 1.0;
  }
  
  abstract void renderObject();
  
  abstract void updateObject();
  
  void render() {
    pushMatrix();
    rotateX(radians(angleX));  
    rotateY(radians(angleY));    
    rotateZ(radians(angleZ));  
    translate(location.x, location.y, location.z);
    scale(scale);
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
  

  protected void applyForce(PVector force) {
    PVector copyForce = PVector.div(force, mass);
    acceleration.add(copyForce); 
  }
  
  
  protected void update() {
    this.updateObject();
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); 
  }
  
  
  protected PVector generateFrictionForce(float mu) {
      PVector friction = velocity.get();
      friction.mult(-1);
      friction.normalize();
      friction.mult(mu);
      return friction;
  }

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


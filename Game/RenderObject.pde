abstract class RenderObject {

  public static final float GRAVITY_CONST = 0.08; 
  public static final float FRICTION_COEF = 0.02;

  
  protected PVector location;
  protected PVector velocity;
  protected PVector gravity;
  protected PVector acceleration;
  protected PVector force_normal;
  protected float mass;
  protected float angleX; 
  protected float angleY; 
  protected float angleZ;
  

  RenderObject() {
    mass = 1.0;
    angleX = 0.0;
    angleY = 0.0;
    angleZ = 0.0;
    location = new PVector(0, 0, 0);
    velocity = new PVector(0, 0, 0);
    acceleration = new PVector(0, 0, 0);
    gravity = new PVector(0, 0, 0);
  }
  
  abstract void renderObject();
  
  abstract void updateObject();
  
  void render() {
    pushMatrix();
    rotateX(radians(angleX));  
    rotateY(radians(angleY));    
    rotateZ(radians(angleZ));  
    translate(location.x, location.y, location.z);
    this.renderObject();
    popMatrix();
  }
  
    
  void update() {  
    this.updateObject();
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); 
  }

  void applyForce(PVector force) {
    PVector copyForce = PVector.div(force, mass);
    acceleration.add(copyForce); 
  }
    
  PVector generateFrictionForce(float mu) {
      PVector friction = velocity.get();
      friction.mult(-1);
      friction.normalize();
      friction.mult(mu);
      return friction;
  }

  void keyPressed() {};
  
  void mouseDragged() {}; 
  
  void mouseWheel(MouseEvent event) {};

  void drawAxes() {
  pushMatrix();
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
   popMatrix();
  }
   
}


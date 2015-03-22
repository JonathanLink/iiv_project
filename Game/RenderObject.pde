abstract class RenderObject {
  
  public static final float GRAVITY_CONST = 0.05; 
  public static final float FRICTION_COEF = 0.01;
  private static final float ZERO_THRESHOLD = 0.01;

  protected PVector location;
  protected PVector velocity;
  protected PVector gravity;
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
    location.add(velocity);
  }

  void applyForce(PVector force) {
    velocity.add(force);
    velocity = applyZeroThreshold(velocity);
  }
  
  private PVector applyZeroThreshold(PVector v) {
    PVector tmpV = v.get();
    if (abs(tmpV.x) < ZERO_THRESHOLD) {
      tmpV.x = 0.0;
    }
    if (abs(tmpV.y) < ZERO_THRESHOLD) {
      tmpV.y = 0.0;
    }
    if (abs(tmpV.z) < ZERO_THRESHOLD) {
      tmpV.z = 0.0;
    }
    return tmpV;
  }
  
  PVector generateFrictionForce(float mu) {
    PVector friction = velocity.get();
    friction.mult(-1);
    friction.normalize();
    friction.mult(mu);
    return friction;
  }

  void keyPressed() {
  };

  void mouseDragged() {
  }; 

  void mouseWheel(MouseEvent event) {
  };

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


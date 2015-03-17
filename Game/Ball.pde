public class Ball extends RenderObject {

  public static final float FRICTION_COEF = 0.01;
  float r;
  color c;
  private Plate plate;
  private PShader shader;

  Ball(Plate plate, float r) {
    this.r = r;
    this.plate = plate;
    location.y = round(-plate.h/2.0 - r);
    c = color(255, 0, 0);
    //shader = loadShader("coloredSphereFrag.glsl", "coloredSphereVert.glsl");
  }

  Ball(Plate plate, float r, color c, float mass) {
    this(plate, r);
    this.c = c;
    super.mass = mass;
  }


  void updateObject() {
    angleX = plate.angleX;
    angleZ = plate.angleZ;
    
    addGrivity();
    addFrictionForce();
    
    checkForObstacles();
    checkEdges();
  }

  private void addGrivity() {
    gravity.x = sin(radians(plate.angleZ)) * GRAVITY;
    gravity.z = -sin(radians(plate.angleX)) * GRAVITY;
    applyForce(gravity);
  }

  private void addFrictionForce() {
    PVector frictionForce = generateFrictionForce(FRICTION_COEF);
    applyForce(frictionForce);
  }


  void renderObject() {
    pushMatrix();
    //shader(shader);
    lights();
    if (DEBUG_MODE) {
      rotateX(radians(frameCount) * 0.8);
      rotateY(radians(frameCount) * 0.8);
      rotateZ(radians(frameCount) * 0.8);
      drawAxes();
    }
    noStroke();
    fill(c);
    sphere(this.r);
    //resetShader();  
    popMatrix();
  }

  private void checkEdges() {
    if (location.x > plate.w/2.0) {
      location.x = plate.w/2.0;
      velocity.x *= -0.5;
    } else if (location.x < -plate.w/2.0) {
      location.x = -plate.w/2.0;
      velocity.x *= -0.5;
    }

    if (location.z > plate.d/2.0) {
      location.z = plate.d/2.0;
      velocity.z *= -0.5;
    } else if (location.z < -plate.d/2.0) {
      location.z = -plate.d/2.0;
      velocity.z *= -0.5;
    }
  }

  private void checkForObstacles() {
    for (Obstacle o : plate.obstacles) {
      // do smth here to rebound the ball if needed
      //println("[checkForObstacles]: " + o);
      // intersection point-cercle ou cercle-cercle ?? (ball-base cylindre)
    }
  }

  protected void drawAxes() {
    // X axe
    stroke(255, 0, 0);
    strokeWeight(2);
    line(0, 0, 0, r * 2, 0, 0);
    line(0, 0, 0, -r * 2, 0, 0);

    // Y axe
    stroke(0, 255, 0);
    strokeWeight(2);
    line(0, r * 2, 0, 0, 0, 0);
    line(0, -r * 2, 0, 0, 0, 0);

    // Z axe
    stroke(0, 0, 255);
    strokeWeight(2);
    line(0, 0, 0, 0, 0, r * 2);
    line(0, 0, 0, 0, 0, -r * 2);
  }

  String toString() {
    return "Ball with r = " + r;
  }
}


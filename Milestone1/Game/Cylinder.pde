public class Cylinder extends PlateObstacleObject {

  static final int CYLINDER_RESOLUTION = 40;
  static final int CYLINDER_RADIUS = 25;
  static final int CYLINDER_HEIGHT = 50;
  static final float MIN_VELOCITY_COLLISION = 0.55;

  protected PShape cylinder;
  protected float radius;
  protected color fillColor;
  private color previousColor;
  private color collisionColor;


  Cylinder(PlateController plateController, float radius, float centerCoordX, float centerCoordZ, color fillColor) {
    super(plateController);
    this.radius = radius;
    this.fillColor = fillColor;
    this.collisionColor = color(255, 255, 255);
    this.previousColor = this.fillColor;
    cylinder = new PShape();
    buildCylinder();
    location.x = centerCoordX;
    location.z = centerCoordZ;
  }

  Cylinder(PlateController plateController, float radius, float centerCoordX, float centerCoordZ) {
    this(plateController, radius, centerCoordX, centerCoordZ, color(70, 220, 30));
  }

  Cylinder(PlateController plateController) {
    this(plateController, Cylinder.CYLINDER_RADIUS, 0, 0);
  }

  private void buildCylinder() {
    float angle;
    float[] x = new float[CYLINDER_RESOLUTION + 1];
    float[] z = new float[CYLINDER_RESOLUTION + 1];
    for (int i = 0; i < x.length; ++i) {
      angle = (TWO_PI / CYLINDER_RESOLUTION) * i;
      x[i] = sin(angle) * radius;
      z[i] = cos(angle) * radius;
    }

    cylinder = createShape();

    cylinder.beginShape(TRIANGLE_FAN);
    for (int i=0; i<x.length; i++) {
      cylinder.vertex(0, -CYLINDER_HEIGHT, 0);
      cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i]);
    }
    cylinder.endShape();

    cylinder.beginShape(TRIANGLE_FAN);
    for (int i=0; i<x.length; i++) {
      cylinder.vertex(0, -this.plate.height/2, 0);
      cylinder.vertex(x[i], -this.plate.height/2, z[i] );
    }
    cylinder.endShape();

    cylinder.beginShape(QUAD_STRIP); 
    for (int i = 0; i < x.length; ++i) {
      cylinder.vertex(x[i], round(-plate.height/2.0), z[i] );
      cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i] );
    } 
    cylinder.endShape();

    cylinder.disableStyle();
  }

  void updateObject() {
    super.updateObject();
  }

  void renderObject() {
    noStroke();
    fill(fillColor);
    shape(cylinder);
    /*pushMatrix();
    rotateX(radians(180));
    translate(0, +plateController.plate.height - 15,0);
    scale(3);
    PShape tree = loadShape("tree.obj");
    shape(tree);
    popMatrix();*/
    
  }

  PVector checkForCollisionWithBall(Ball ball) {
    PVector distanceBetweenCenters = PVector.sub(ball.location, location);    
    distanceBetweenCenters.y = 0;

    //test for collisions
    if (distanceBetweenCenters.mag() < ball.radius + radius ) {

      // user win some points proportioned to the velocity of the ball
      if (ball.velocity.mag() >= MIN_VELOCITY_COLLISION) {
        if (fillColor != collisionColor) {
          previousColor = fillColor;
          fillColor = collisionColor;
        }
        addPoints(ball.velocity);
      }

      //normalize the vector given by the two centers at the time of the collision
      distanceBetweenCenters.normalize();
      //create the normal vector
      PVector normal = distanceBetweenCenters.get();
      //compute the cos angle between velocity and normal
      float cos = normal.dot(ball.velocity)/(normal.mag()*ball.velocity.mag());

      //test if the velocity heads to the cylinder and update the velocity
      if (cos < 0) {
        float angle = 2*normal.dot(ball.velocity);
        PVector temp = PVector.mult(normal, angle);
        //computation of the new velocity
        ball.velocity = PVector.sub(ball.velocity, temp);
        //lost of energy after hitting the cylinder
        ball.velocity = PVector.mult(ball.velocity, 0.5);
      }
      return normal;
    } else {
      this.fillColor = previousColor;
      return new PVector(0, 0, 0);
    }
  }

  void addPoints(PVector velocity) {
    String text = "+"+ round(velocity.mag())+" PTS!";
    PointsText pointsText = new PointsText(plateController, text, location.x, location.y - CYLINDER_HEIGHT, location.z);
    plateController.addAnimatedTextPlate(pointsText);
    consoleLayer.write("ball velocity:"+velocity.mag());
  }

  void draw2D(PGraphics pGraphics) {
    pGraphics.stroke(fillColor);
    pGraphics.fill(fillColor);
    pGraphics.ellipse(location.x, location.z, 2*radius, 2*radius);
  }

  String toString() {
    return "Cylinder with radius = " + radius ;
  }
}


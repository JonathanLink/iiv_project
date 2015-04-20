public class Ball extends RenderObject {

  private Plate plate;
  private float radius;
  private color c;

  Ball(Plate plate, float radius) {
    this.plate = plate;
    this.radius = radius;
    c = color(255, 0, 0);
    //shift in y-direction to place the ball on the plate
    location.y = round(-plate.h/2.0 - radius);
  }

  Ball(Plate plate, float radius, color c, float mass) {
    this(plate, radius);
    this.c = c;
    super.mass = mass;
  }

  void updateObject() {
    angleX = plate.angleX;
    angleZ = plate.angleZ;

    checkForEdges();
    checkCylinderCollision();

    addGravity();
    addFrictionForce();
    
  }

  private void addGravity() {
    gravity.x = sin(radians(angleZ)) * GRAVITY_CONST;
    gravity.z = -sin(radians(angleX)) * GRAVITY_CONST;
    applyForce(gravity);
  }

  private void addFrictionForce() {
    PVector frictionForce = generateFrictionForce(FRICTION_COEF);
    applyForce(frictionForce);
  }


  void renderObject() {
    lights();
    noStroke();
    fill(c);
    sphere(this.radius);
  }

  private void checkForEdges() {
    if (location.x  > plate.w/2.0 - radius) {
      location.x = plate.w/2.0 - radius;
      velocity.x *= -0.5;
    } else if (location.x < -plate.w/2.0 + radius) {
      location.x = -plate.w/2.0 + radius;
      velocity.x *= -0.5;
    }

    if (location.z  > plate.d/2.0 - radius) {
      location.z = plate.d/2.0 - radius;
      velocity.z *= -0.5;
    } else if (location.z < -plate.d/2.0 + radius) {
      location.z = -plate.d/2.0 + radius;
      velocity.z *= -0.5;
    }
  }


  private void checkCylinderCollision() {

    if (!obstacles.isEmpty()) {
      PVector distanceBetweenCenters;

      PVector totalNormal = new PVector(0, 0, 0);
      for (Cylinder o : obstacles) {

        distanceBetweenCenters = PVector.sub(this.location, o.location);    
        distanceBetweenCenters.y = 0;

        //test for collisions
        if (distanceBetweenCenters.mag() < BALL_RADIUS+CYLINDER_RADIUS ) {

          //normalize the vector given by the two centers at the time of the collision
          distanceBetweenCenters.normalize();
          //create the normal vector
          PVector normal = distanceBetweenCenters.get();
          //compute the cos angle between velocity and normal
          float cos = normal.dot(velocity)/(normal.mag()*velocity.mag());

          //test if the velocity heads to the cylinder and update the velocity
          if (cos < 0) {
            float angle = 2*normal.dot(velocity);
            PVector temp = PVector.mult(normal, angle);
            //computation of the new velocity
            velocity = PVector.sub(velocity, temp);
            //lost of energy after hitting the cylinder
            velocity = PVector.mult(velocity, 0.5);
            
          }

          // add normal force of the current obstacle
          totalNormal.add(normal);

        }
      }

      //apply the normal force
      if (totalNormal.mag() > 0) {
        totalNormal.setMag(gravity.mag());
        applyForce(totalNormal);
      }
    }
  }

  void drawAxes() {
    // X axe
    stroke(255, 0, 0);
    strokeWeight(2);
    line(0, 0, 0, radius * 2, 0, 0);
    line(0, 0, 0, -radius * 2, 0, 0);

    // Y axe
    stroke(0, 255, 0);
    strokeWeight(2);
    line(0, radius * 2, 0, 0, 0, 0);
    line(0, -radius * 2, 0, 0, 0, 0);

    // Z axe
    stroke(0, 0, 255);
    strokeWeight(2);
    line(0, 0, 0, 0, 0, radius * 2);
    line(0, 0, 0, 0, 0, -radius * 2);
  }

  String toString() {
    return "Ball with radius = " + radius;
  }
}


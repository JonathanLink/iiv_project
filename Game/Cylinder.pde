public class Cylinder extends RenderObject implements Obstacle {

  private final int cylinderResolution = 40;
  private float cylinderBaseSize;
  private float cylinderHeight;
  private PShape cylinder;
  private Plate plate;

  Cylinder(Plate plate, float cylinderBaseSize) {
    this.plate = plate;
    this.cylinderBaseSize = cylinderBaseSize;
    cylinderHeight = 100;
    cylinder = new PShape();

    buildCylinder();
  }
  
  Cylinder(Plate plate) {
    this(plate, 25);
  }

  private void buildCylinder() {
    float angle;

    float[] x = new float[cylinderResolution + 1];
    float[] z = new float[cylinderResolution + 1];
    for (int i = 0; i < x.length; ++i) {
      angle = (TWO_PI / cylinderResolution) * i;
      x[i] = sin(angle) * cylinderBaseSize;
      z[i] = cos(angle) * cylinderBaseSize;
    }
    cylinder = createShape();
    cylinder.beginShape(QUAD_STRIP); // TRIANGLES or TRIANGLE_FAN or QUAD_STRIP
    for (int i = 0; i < x.length; ++i) {
      cylinder.vertex(x[i], round(- plate.h/2.0), z[i] );
      cylinder.vertex(x[i], -cylinderHeight, z[i] );
    }
    cylinder.noStroke();
    cylinder.endShape();
  }

  void updateObject() {
    angleX = plate.angleX;
    angleZ = plate.angleZ;
  }

  void renderObject() {
    pushMatrix();
    //translate(40*sin(radians(frameCount%360)), 0, 40*cos(radians(frameCount%360)) );
    //drawAxes();
    shape(cylinder);
    popMatrix();
  }



  String toString() {
    return "Cylinder with cylinderBaseSize = " + cylinderBaseSize ;
  }
}


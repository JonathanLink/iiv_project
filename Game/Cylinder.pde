public class Cylinder extends RenderObject {

  private Plate plate;
  private PShape cylinder;
  private int cylinderResolution ;
  private float cylinderBaseSize;
  private color c;
  float centerCoordX;
  float centerCoordZ;


  Cylinder(Plate plate, float cylinderBaseSize, float centerCoordX, float centerCoordZ) {
    this.plate = plate;
    this.cylinderBaseSize = cylinderBaseSize;
    this.centerCoordX = centerCoordX;
    this.centerCoordZ = centerCoordZ;
    cylinderResolution = 40;
    c = color(70, 220, 30);
    cylinder = new PShape();
    buildCylinder();
    location.x = centerCoordX;
    location.z = centerCoordZ;
  }

  Cylinder(Plate plate, float cylinderBaseSize) {
    this(plate, cylinderBaseSize, 0, 0);
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
    
    fill(c);
    
    cylinder = createShape();
    cylinder.beginShape(TRIANGLE_FAN);
    for (int i=0; i<x.length; i++) {
      cylinder.vertex(0, -CYLINDER_HEIGHT, 0);
      cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i]);
    }
    cylinder.beginShape(TRIANGLE_FAN);
    for (int i=0; i<x.length; i++) {
      cylinder.vertex(0, -this.plate.h/2, 0);
      cylinder.vertex(x[i], -this.plate.h/2, z[i] );
    }
    cylinder.beginShape(QUAD_STRIP); 
    for (int i = 0; i < x.length; ++i) {
      cylinder.vertex(x[i], round(-plate.h/2.0), z[i] );
      cylinder.vertex(x[i], -CYLINDER_HEIGHT, z[i] );
    } 
    cylinder.noStroke();
    cylinder.endShape();
    
  }

  void updateObject() {
    angleX = plate.angleX;
    angleZ = plate.angleZ;
  }

  void renderObject() {
    shape(cylinder);
  }


  String toString() {
    return "Cylinder with cylinderBaseSize = " + cylinderBaseSize ;
  }
}


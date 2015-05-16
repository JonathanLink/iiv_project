public class Plate extends RenderObject {

  static final int PLATE_WIDTH = 500;
  static final int PLATE_HEIGHT = 40;
  static final int PLATE_DEPTH = 500;

  color fillColor;
  float width;
  float height;
  float depth;
  float speed;


  Plate() {
    this(Plate.PLATE_WIDTH, Plate.PLATE_HEIGHT, Plate.PLATE_DEPTH);
  }

  Plate(float width, float height, float depth) {
    this.width = width;
    this.height = height;
    this.depth = depth;
    speed = 1.0;
    fillColor = color(192, 192, 192);
  }

  void updateObject() {
  }

  void renderObject() {
    stroke(140.0, 140.0, 140.0);
    if (angleX > 0.0) {
      //transparent tilted plate to see the ball
      noFill();
    } else {
      fill(fillColor);
    }
    box(this.width, this.height, this.depth);
    if (DEBUG_MODE) drawAxes();
  }
  
  void draw2D(PGraphics pGraphics) {
    pGraphics.stroke(fillColor);
    pGraphics.fill(fillColor);
    pGraphics.rect(0, 0, this.width, this.depth);
  }


  String toString() {
    return "I' am the plate!";
  }
}


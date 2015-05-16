abstract class Layer {

  protected PGraphics layer;

  abstract void drawMyLayer();
  abstract int getX();
  abstract int getY();
  
  protected int width;
  protected int height;

  Layer() {
    this(displayWidth, 250);
  }

  Layer(int height) {
    this(displayWidth, height);
  }

  Layer(int width, int height) {
    this.width = width;
    this.height = height;
    layer = createGraphics(this.width, this.height, P3D);
  }

  void draw() {
    pushMatrix();
    translate(-displayWidth/2.0, -displayHeight/2.0, 0);
    drawMyLayer();
    image(layer, getX(), getY());
    popMatrix();
  }
  

}


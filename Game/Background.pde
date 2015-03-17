
public class Background extends RenderObject {

  private final int STEP = 62 / 2;

  private float wsf;
  Background(float wsf) {
    this.wsf = wsf;
  }

  void renderObject() {
    pushMatrix();
    background(0, 0, 0);
    translate(-width/2, -height/2);

    rotateX(radians(60));
    drawGrid(-300 * wsf);
    popMatrix();


    pushMatrix();
    translate(-width/2, -height/2 - 300);
    rotateY(radians(frameCount % 180));
    rotateZ(radians(frameCount % 180));
    translate(0, - height);
    rotateX(radians(frameCount % 180));
    drawGrid(-1000 * wsf);


    popMatrix();
  }

  void updateObject() {
  }

  private void drawGrid(float z) {
    float z1 = z;
    float z2 = z1;

    float x1 = 0;
    for (int i = 0; i <= ceil (width *wsf / STEP); ++i) {
      float alpha = i * (-255.0/ceil(width* wsf / STEP)) + 255.0;
      stroke(0, 255, 0, alpha);
      line(x1, 0, z1, x1, height* wsf, z2); // vertical line
      x1 += STEP;
    }


    float y1 = 0;
    for (int j = 0; j <= ceil (height* wsf / STEP); ++j ) {
      float alpha = j * (255.0/ceil(height * wsf / STEP));
      stroke(0, 255, 0, alpha);
      line(0, y1, z1, width * wsf, y1, z2); // horizontal line
      y1 += STEP;
    }
  }




  protected void drawAxes() {
  }
}


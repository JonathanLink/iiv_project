abstract class Controller {
  abstract void draw();

  void keyPressed() {
  }

  void keyReleased() {
  }

  void mouseDragged() {
  }

  void mouseWheel(MouseEvent event) {
  }

  void mouseClicked() {
  }

  void setOrigin(float x, float y, float z) {
    translate(x, y, z);
  }
}


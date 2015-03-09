public class Plate extends RenderObject {

  final private float SPEED = 5 * PI / 180.0;
  final private float LIMIT_ANGLE = PI / 3.0;

  private float w = 0.0;
  private float h = 0.0;
  private float d = 0.0;

  Plate(Game game, float w, float h, float d) {
    super(game);
    this.w = w;
    this.h = h;
    this.d = d;
  }

  void renderObject() {
    stroke(0.0, 0.0, 0.0);
    fill(255, 255, 255);
    box(this.w, this.h, this.d);
  }


  void keyPressed() {
    if (game.key == CODED) {
      switch(game.keyCode) {
      case LEFT:
        super.rotateY -= SPEED;
        break;
      case RIGHT:
         super.rotateY += SPEED;
        break;
      }
    }
  }
  
  

  void mouseDragged() {


    float mouseYDiff = mouseY - pmouseY;
    float mouseXDiff = mouseX - pmouseX;

    if ((rotateX + SPEED) < LIMIT_ANGLE) {
      if (mouseYDiff > 0) {
        rotateX += SPEED;
      }
    } 

    if ((rotateX - SPEED) > -LIMIT_ANGLE) {
      if (mouseYDiff < 0) {
        rotateX -= SPEED;
      }
    }
 

    if ((rotateZ + SPEED) < LIMIT_ANGLE) {
      if (mouseXDiff > 0) {
        rotateZ += SPEED;
      }
    } 

    if ((rotateZ - SPEED) > -LIMIT_ANGLE) {
      if (mouseXDiff < 0) {
        rotateZ -= SPEED;
      }
    }
    
    
    
  }
  
}

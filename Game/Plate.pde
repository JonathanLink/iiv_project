public class Plate extends RenderObject  {
  
  float w;
  float h;
  float d;
  
  float rotateX;
  float rotateY;
  float rotateZ;
  
  Plate(Game game, float w, float h, float d) {
    super(game);
    this.w = w;
    this.h = h;
    this.d = d; 
  }
  
  void render() {
    pushMatrix();
    rotateX(this.rotateX);
    rotateY(this.rotateY);
    rotateZ(this.rotateZ);
    stroke(0.0, 0.0, 0.0);
    box(this.w, this.h , this.d);
    popMatrix();  
  }
  
  void keyPressed() {
    if (game.key == CODED) {
      switch(game.keyCode) {

        case LEFT:
          rotateY += 0.1;  
          break;
          
        case RIGHT:
          rotateY -= 0.1; 
          break; 
       }
    }
  }
  
  void mouseDragged() {
    rotateX += 0.1; 
    //rotateZ += 0.1;
  }
  

  
  
}

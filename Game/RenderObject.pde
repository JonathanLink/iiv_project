abstract class RenderObject {
  
  protected Game game;
  
  RenderObject(Game game) {
    this.game = game; 
  }
  
  abstract void render();
  
  void keyPressed() {};
  void mouseDragged() {}; 
  // mouseWheel()...
}

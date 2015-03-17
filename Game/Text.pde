public class Text extends RenderObject {
  
  String text;
  
  Text(String text) { 
    this.text = text;
  }
  
  void renderObject() {
    pushMatrix();
    //translate(frameCount % width, 0, 0);
    textSize(20);
    fill(0, 0, 0);
    text(text, 0, 0, 0);
    popMatrix();
  }

  void updateObject() {
   
  }
  
  void drawAxes() {
    
  }
  
  String toString() {
    return "Text (" + text + ")" ;
  }
  
}


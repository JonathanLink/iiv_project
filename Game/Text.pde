public class Text extends RenderObject {
  
  String text;
  
  Text(String text) { 
    this.text = text;
  }
  
  void renderObject() {
    pushMatrix();
    textSize(20);
    fill(0, 0, 0);
    text(text, 0, 0, 0);
    popMatrix();
  }

  void updateObject() {
     
  }
  
  void drawAxes() {
   
  }
  
  void updateText(String text) {
    this.text = text;
  }
  
  String toString() {
    return "Text (" + text + ")" ;
  }
  
}


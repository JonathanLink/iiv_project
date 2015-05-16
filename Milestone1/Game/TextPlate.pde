public class TextPlate extends PlateObject {

  String text;
  float fontSize;
  String fontPolice;
  color fontFillColor;
  color fontStrokeColor;
  float x;
  float y;
  float z;

  TextPlate(PlateController plateController, String text, float x, float y, float z) {
    super(plateController); 
    this.text = text;
    this.fontSize = 20;
    this.fontPolice = "Lucida Sans";
    this.fontFillColor = color(0, 0, 0);
    this.fontStrokeColor = color(255, 255, 255);
    this.x = x;
    this.y = y;
    this.z = z;
  }

  void renderObject() {
    textSize(fontSize);
    fill(fontFillColor);
    stroke(fontStrokeColor);
    text(text, x, y, z);
  }

  void updateObject() {
    super.updateObject();
  }

  void drawAxes() {
  }

  void draw2D(PGraphics pGraphics) {
  }

  String toString() {
    return "TextPlate (" + text + ")" ;
  }
  
  
}


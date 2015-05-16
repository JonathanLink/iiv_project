class PointsText extends AnimatedTextPlate {

  static final float Y_SPEED = 1.0;
  static final float RELATIVE_MAX_Y = 100.0;

  private float relativeMaxY = 0.0;
  private float alphaFont = 255.0;

  PointsText(PlateController plateController, String text, float x, float y, float z) {
    super(plateController, text, x, y, z);
  }

  void renderObject() {
    textSize(fontSize);
    fill(fontFillColor);
    stroke(fontStrokeColor);
    text(text, x, y, z);
  }

  void updateObject() {
    super.updateObject();
    if (!isAnimationFinished()) {
      location.y = location.y - Y_SPEED;
      relativeMaxY = relativeMaxY + Y_SPEED;
      float red = fontFillColor >> 16 & 0xFF; // faster than fontFillColor.red() (optimizazion purpose)
      float green = fontFillColor >> 8 & 0xFF;
      float blue = fontFillColor >> 0xFF;
      alphaFont = (-255.0 / RELATIVE_MAX_Y) * relativeMaxY + 255.0;
      fontFillColor = color(red, green, blue, alphaFont);
    }
  }

  boolean isAnimationFinished() {
    return (relativeMaxY >= RELATIVE_MAX_Y);
  }
}


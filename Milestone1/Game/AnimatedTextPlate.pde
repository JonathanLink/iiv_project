abstract class AnimatedTextPlate extends TextPlate {
  AnimatedTextPlate(PlateController plateController, String text, float x, float y, float z) {
    super(plateController, text, x, y, z);
  }
  abstract boolean isAnimationFinished();
}

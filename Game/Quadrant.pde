interface Quadrant {
  
  static final int QUADRANT_ZERO = 0;
  static final int QUADRANT_ONE = 1;
  static final int QUADRANT_TWO = 2;
  static final int QUADRANT_THREE = 3;
   
  static final float INITIAL_SHIFT = - QUARTER_PI;
  static final float QUADRANT_ONE_MIN = INITIAL_SHIFT;
  static final float QUADRANT_ONE_MAX = INITIAL_SHIFT + HALF_PI;
  static final float QUADRANT_TWO_MIN = QUADRANT_ONE_MAX;
  static final float QUADRANT_TWO_MAX = INITIAL_SHIFT + PI;
  static final float QUADRANT_THREE_MIN = QUADRANT_TWO_MAX;
  static final float QUADRANT_THREE_MAX = INITIAL_SHIFT + (3.0 * HALF_PI);
  static final float QUADRANT_FOUR_MIN = QUADRANT_THREE_MAX;
  static final float QUADRANT_FOUR_MAX = INITIAL_SHIFT + TWO_PI; 
  
  int getCurrentQuadrant();
  
}

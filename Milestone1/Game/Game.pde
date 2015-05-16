/*
Project: 3D game made with Processing library
 Course: EPFL - (Introduction to Visual Computing)
 Authors: Céline Dupuis, Romain Gallay, Jonathan Link
 Date: 2015 - spring semester
 */


final static int FRAME_RATE = 30;
final static boolean DEBUG_MODE = true;
final static int PLATE_MODE = 0;
final static int EDIT_MODE = 1;


// Controllers
PlateController plateController;
EditController editController;
Controller currentController;

int currentMode;

// Layers
ConsoleLayer consoleLayer;
DataVisualizationLayer dataVisualizationLayer;

void setup() {
  size(displayWidth, displayHeight, P3D);
  frameRate(FRAME_RATE);

  // Init plateController
  plateController = new PlateController();

  // Init EditController
  editController = new EditController(plateController);

  // Set current mode
  this.setMode(PLATE_MODE);

  // Set data visualization layer
  dataVisualizationLayer = new DataVisualizationLayer(plateController); 

  // Set console
  consoleLayer = new ConsoleLayer();
}

void setCamera() {
  //default camera 
  //camera(displayWidth/2.0, displayHeight/2.0, ((displayHeight/2.0) / tan(PI*30.0 / 180.0)), displayWidth/2.0, displayHeight/2.0, 0, 0, 1, 0);
  camera();
}

void setLight() {
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(200, 200, 200);
}

void setBackground() {
  background(255.0, 255.0, 255.0);
}

void draw() {
  cursor(ARROW);

  setCamera();
  setLight();
  setBackground();

  currentController.draw();
  if (DEBUG_MODE) consoleLayer.draw();
  dataVisualizationLayer.draw();
  
}

void setMode(int mode) {
  switch(mode) {
  case PLATE_MODE:
    currentMode = PLATE_MODE;
    currentController = plateController; 
    break;
  case EDIT_MODE:
    currentMode = EDIT_MODE;
    currentController = editController;
    break;
  default:
    println("[ERROR] MODE DOES NOT EXIST!");
  }
}

PGraphics getDefaultPGraphics() {
  return this.g; 
}

/*
Project: 3D game made with Processing library
Course: EPFL - (Introduction to Visual Computing)
Authors: Céline Dupuis, Romain Gallay, Jonathan Link
Date: 2015 - spring semester
*/

final int WINDOW_WIDTH = 500;
final int WINDOW_HEIGHT = 500;
final int FRAME_RATE = 60;

Plate plate;

void setup() {
  size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D); 
  noStroke();
  frameRate(FRAME_RATE);
  initPlate();
}

void setCamera() {
  /*
  camera( width/2, height/2, 500,
          width/2,  height/2, 0,
          0, 1, 0);
  */
 camera(); 
}

void setLight() {
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(102, 102, 102);  
}

void setBackground() {
  background(200); 
}

void setOrigin(float x, float y, float z) {
  translate(x, y, z); 
}

void initPlate() {
  this.plate = new Plate(width/2, 10, width/2); 
}

void renderPlate() {
  if (this.plate != null) {
    this.plate.render(); 
  } 
}

void draw() {
  setCamera();
  setLight();
  setBackground();
  setOrigin(width/2, height/2, 0);
  renderPlate();
}

void keyPressed() {
  plate.keyPressed();
}
 
void mouseDragged(MouseEvent event) {
  plate.mouseDragged(event);
}

void mouseWheel(MouseEvent event) {
  plate.mouseWheel(event);
}
 

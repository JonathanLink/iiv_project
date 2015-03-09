/*
Project: 3D game made with Processing library
Course: EPFL - (Introduction to Visual Computing)
Authors: Céline Dupuis, Romain Gallay, Jonathan Link
Date: 2015 - spring semester
*/

final boolean DEBUG_MODE = true;
final int FRAME_RATE = 60;

Plate plate;

void setup() {
  size(displayWidth, displayHeight, P3D); 
  noStroke();
  frameRate(FRAME_RATE);
  initPlate();
}

void setCamera() {
  
  camera( width/2, height/2, 4000,
          width/2,  height/2, 0,
          0, 1, 0);
    
   //camera(); 
}

void setLight() {
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(102, 102, 102);  
}

void setBackground() {
  background(255.0, 255.0, 255.0); 
}

void setOrigin(float x, float y, float z) {
  translate(x, y, z); 
}

void initPlate() {
  this.plate = new Plate(this, width/2, 40, width/2); 
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
  this.plate.keyPressed();
}
 
void mouseDragged() {
  this.plate.mouseDragged();
}


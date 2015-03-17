/*
Project: 3D game made with Processing library
 Course: EPFL - (Introduction to Visual Computing)
 Authors: Céline Dupuis, Romain Gallay, Jonathan Link
 Date: 2015 - spring semester
 */


final boolean DEBUG_MODE = true;
final int FRAME_RATE = 24;

ArrayList <RenderObject> objects; 
boolean  edit_mode = false;
EditModeController editModeController;

void setup() {
  int windowWidth = (DEBUG_MODE)? 500 : displayWidth;
  int windowHeight = (DEBUG_MODE)? 500 : displayHeight;
  size(windowWidth, windowHeight, P3D);
  frameRate(FRAME_RATE);

  float wsf = (DEBUG_MODE)? 1.0 : displayWidth * 0.001; // window size factor (à mettre dans le resize de RenderObject?)

  objects = new ArrayList<RenderObject>();

  Background background = new Background(wsf);
  //addObject(background); // uncomment this line to see an amazing background de ouf.

  Plate plate = new Plate(300 * wsf, 20 * wsf, 300 * wsf ); 
  addObject(plate); 

  Ball ball = new Ball(plate, 10 * wsf); 
  addObject(ball);

  Cylinder cylinder = new Cylinder(plate); 
  addObject(cylinder);
  plate.addObstacle(cylinder);
  //cylinder.location.x = 120;

  editModeController = new EditModeController();

  if (DEBUG_MODE) {
    Ball ball2 = new Ball(plate, 10 * wsf, color(0, 255, 0), 4.0); 
    addObject(ball2);

    Ball ball3 = new Ball(plate, 20 * wsf, color(0, 0, 255), 8.0); 
    addObject(ball3);

    Text text = new Text("[DEBUG MODE]"); 
    text.location.x = -width/2.0;
    text.location.y = -200;
    addObject(text);
  }
}

void setCamera() {
  if (DEBUG_MODE) {
    camera();
  } else {
    camera();
  }
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


void renderAllObjects() {
  for (RenderObject o : objects) {
    o.render();
  }
}

void updateAllObjects() {
  for (RenderObject o : objects) {
    o.update();
  }
}

void addObject(RenderObject object) {
  objects.add(object);
}

void draw() {
  setCamera();
  setLight();
  setBackground();
  setOrigin(width/2, height/2, 0);

  if (!edit_mode) {
    updateAllObjects();
    renderAllObjects();
  } else {
    editModeController.render();
  }
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == SHIFT) {
      edit_mode = true;
    }
  }

  for (RenderObject o : objects) {
    o.keyPressed();
  }
}

void keyReleased() {
  if (edit_mode) {
    if (key == CODED && keyCode == SHIFT) {
      edit_mode = false;
    }
  }
}


void mouseDragged() {
  if (!edit_mode) {
    for (RenderObject o : objects) {
      o.mouseDragged();
    }
  }
}

void mouseWheel(MouseEvent event) {
  if (!edit_mode) {
    for (RenderObject o : objects) {
      o.mouseWheel(event);
    }
  }
}

void mouseClicked() {
  if (edit_mode) {
    editModeController.mouseClicked();
  }
}

/*boolean sketchFullScreen() {
 return (DEBUG_MODE == false);
 }*/


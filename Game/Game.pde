/*
Project: 3D game made with Processing library
Course: EPFL - (Introduction to Visual Computing)
Authors: Céline Dupuis, Romain Gallay, Jonathan Link
Date: 2015 - spring semester
*/

//Variables for the display
final int FRAME_RATE = 60;
final boolean DEBUG_MODE = false;
int windowWidth;
int windowHeight;
boolean EDIT_MODE = false;
Text speedOfTilt;


//Variables for the game
Ball ball;
Plate plate;
final int PLATE_WIDTH = 500;
final int PLATE_HEIGHT = 40;
final int PLATE_DEPTH = 500;
final int BALL_RADIUS = 15;
final int CYLINDER_RADIUS = 25;
final int CYLINDER_HEIGHT = 100;

ArrayList <RenderObject> objects; 
ArrayList <Cylinder> obstacles;
EditModeController editModeController;


void setup() {
  
  windowWidth = (DEBUG_MODE)? 900 : displayWidth;
  windowHeight = (DEBUG_MODE)? 700 : displayHeight;
  size(windowWidth, windowHeight, P3D);
  frameRate(FRAME_RATE);
  
  objects = new ArrayList<RenderObject>();
  obstacles = new ArrayList<Cylinder>();
  editModeController = new EditModeController();

  //Initialize the plate and add it to the object array
  plate = new Plate(PLATE_WIDTH, PLATE_HEIGHT, PLATE_DEPTH); 
  addObject(plate); 
  
  //Initialize the ball add it to the object array
  ball = new Ball(plate, BALL_RADIUS); 
  addObject(ball);

  if (DEBUG_MODE) {
    /*Ball ball2 = new Ball(plate, BALL_RADIUS, color(0, 255, 0), 2.0); 
    addObject(ball2);

    Ball ball3 = new Ball(plate, 2*BALL_RADIUS, color(0, 0, 255), 2.0); 
    addObject(ball3);*/

    Text text = new Text("[DEBUG MODE]"); 
    text.location.x = -windowWidth/2.0;
    text.location.y = -windowHeight/2.0 + 20;
    addObject(text);
    
    speedOfTilt = new Text("Speed of tilt: " + nf(plate.speed,1,2));
    speedOfTilt.location.x = -windowWidth/2.0;
    speedOfTilt.location.y =  -windowHeight/2.0 + 50;
    addObject(speedOfTilt);

  }  
}

void setCamera() {
   //default camera 
   camera(windowWidth/2.0, windowHeight/2.0, ((windowHeight/2.0) / tan(PI*30.0 / 180.0)), windowWidth/2.0, windowHeight/2.0, 0, 0, 1, 0);
}

void setLight() {
  directionalLight(50, 100, 125, 0, -1, 0);
  ambientLight(200, 200, 200);
}

void setBackground() {
  background(255.0, 255.0, 255.0);
}

void setOrigin(float x, float y, float z) {
  translate(x, y, z);
}


void draw() {
  cursor(ARROW);
  
  setCamera();
  setLight();
  setBackground();
  
  //the origin of the game is at the middle point of the window
  setOrigin(windowWidth/2, windowHeight/2, 0);
  
  if (!EDIT_MODE) { 
    updateAllObjects();
    renderAllObjects();
  } else {
    editModeController.render();
  }
  
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

void addObstacle(Cylinder object) {
  obstacles.add(object);
}

PVector coord2Dto3D (int coordX, int coordY) {
  return new PVector(coordX,0,coordY);
}


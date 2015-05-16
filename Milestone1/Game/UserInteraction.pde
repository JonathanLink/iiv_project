
void keyPressed() {
  if (key == CODED && keyCode == SHIFT) {
    this.setMode(EDIT_MODE);
  }
  currentController.keyPressed();
}

void keyReleased() {
  if (key == CODED && keyCode == SHIFT) {
    this.setMode(PLATE_MODE);
  }
  currentController.keyReleased();
}

void mouseDragged() {
  currentController.mouseDragged();
}

void mouseWheel(MouseEvent event) {
  currentController.mouseWheel(event);
}

void mouseClicked() {
  currentController.mouseClicked();
}


//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
int getCoordX() {
  return round(mouseX - displayWidth/2.0);
}

int getCoordY() {
  return round(mouseY - displayHeight/2.0);
}


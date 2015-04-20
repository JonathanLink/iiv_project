void keyPressed() {
  if (key == CODED && keyCode == SHIFT) {
      EDIT_MODE = true;
      for (RenderObject o : objects) {
        o.keyPressed();
      }
  }
}

void keyReleased() {
  if (EDIT_MODE) {
    if (key == CODED && keyCode == SHIFT) {
      EDIT_MODE = false;
    }
  }
}


void mouseDragged() {
  if (!EDIT_MODE) {
    for (RenderObject o : objects) {
      o.mouseDragged();
    }
  }
}

void mouseWheel(MouseEvent event) {
  if (!EDIT_MODE) {
    for (RenderObject o : objects) {
      o.mouseWheel(event);
    }
  }
}

void mouseClicked() {
  if (EDIT_MODE) {
    editModeController.mouseClicked();
  }
}


//methods that return the mouse click in the game coordinates, i.e with the origin in the middle of the window
int getCoordX() {
  return mouseX - windowWidth/2;
}

int getCoordY() {
  return mouseY - windowHeight/2;
}

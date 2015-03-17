class EditModeController {

  void render() {
    //render edit mode rectangle with cylinders
    textSize(20);
    fill(255.0, 0, 0);
    String msg = "HERE WILL BE THE EDIT MODE MOTHER FUCK'R !";
    text(msg, width/2.0 - textWidth(msg), 0, 0);
  }

  void mouseClicked() {
    println("mouse clicked from EditModeController");
  }
  
}


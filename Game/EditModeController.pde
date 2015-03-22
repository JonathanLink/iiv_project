class EditModeController {
  
  int coordX;
  int coordY;
  
  void render() {
 
    //draw the plate in 2D
    pushMatrix();
    translate(-plate.w/2.0,-plate.d/2.0);
    fill(plate.c);
    rect(0,0,plate.w,plate.d);
    popMatrix();
   
    //draw the ball in 2D
    fill(ball.c);
    ellipse(ball.location.x, ball.location.z,2*ball.radius,2*ball.radius);
   
    //draw the cylinders in 2D
    if(!obstacles.isEmpty()) {
      for (Cylinder o : obstacles) {
       fill(o.c);
       ellipse(o.centerCoordX, o.centerCoordZ,2*CYLINDER_RADIUS,2*CYLINDER_RADIUS);
      }
    }
    
    //test if the mouse click is on the grey rectangle to display the cursor
    if (getCoordX() < plate.w/2 &&
          getCoordX() > -plate.w/2 &&
          getCoordY() > -plate.d/2 &&
          getCoordY() < plate.d/2) {
          cursor(CROSS);
    }
  }

  void mouseClicked() {
    
      //test if the mouse click is on the grey rectangle
      if (getCoordX() < plate.w/2 &&
          getCoordX() > -plate.w/2 &&
          getCoordY() > -plate.d/2 &&
          getCoordY() < plate.d/2) {
            
          coordX = getCoordX();
          coordY = getCoordY();
          
          PVector center = new PVector(coordX,0,coordY);
          PVector distanceBetweenCylinders;
          PVector distanceToTheBall;
          boolean overlap = false;
          
          //Prevent to add a cylinder on the ball
          distanceToTheBall = PVector.sub(center,ball.location); 
          distanceToTheBall.y = 0; 
          if (distanceToTheBall.mag() < CYLINDER_RADIUS + ball.radius) {
             overlap = true;
              println("ERROR: you cannot add a cylinder here,it would overlap with the ball");
          }
          
          //Prevent to add a cylinder which could overlapp another cylinder
          if(!obstacles.isEmpty()) {
            for (Cylinder o : obstacles) {
              distanceBetweenCylinders = PVector.sub(center,o.location);      
              if (distanceBetweenCylinders.mag() < 2.0*CYLINDER_RADIUS ) {
                overlap = true;
                println("ERROR: you cannot add a cylinder here,it would overlap another cylinder");
              }
            }
          }
          
          if(!overlap) {
            //Create a cylinder with the clicked location as center
            Cylinder cylinder = new Cylinder(plate,CYLINDER_RADIUS,coordX,coordY);
            //Add the cylinder on the plate
            addObject(cylinder);
            //Add the cylinder on the list of obstacle
            addObstacle(cylinder);
            render();
          } 
      }
  }
 
}


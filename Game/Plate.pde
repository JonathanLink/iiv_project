public class Plate extends RenderObject {
  float angleX = 0;
  float angleZ = 0;
  float w;
  float h;
  float d;

  float rotateX;
  float rotateY;
  float rotateZ;

  float speed = 0.05;
  float jump = 0.005;
  float coeff = 0.05;

  Plate(Game game, float w, float h, float d) {
    super(game);
    this.w = w;
    this.h = h;
    this.d = d;
  }

  void render() {
    pushMatrix();

    rotateX(this.angleX);
    rotateZ(this.angleZ);

    rotateX(this.rotateX);
    rotateY(this.rotateY);
    rotateZ(this.rotateZ);
    stroke(0.0, 0.0, 0.0);
    box(this.w, this.h, this.d);

    popMatrix();
  }

  void keyPressed() {
    if (game.key == CODED) {
      switch(game.keyCode) {

      case LEFT:
        rotateY += speed;  
        break;

      case RIGHT:
        rotateY -= speed; 
        break; 

      case UP:
        rotateX += speed;
        break;

      case DOWN:
        rotateX -=speed;
        break;
      }
    } else if (key == 'w') {
      speed += jump;
    } else if (key == 's' && speed > jump) {
      speed -=jump;
    }
  }

  void mouseWheel(MouseEvent event) {
    float e = event.getCount();
    if (e>0 && speed > 0) {
      speed -= jump;
    } else if (e<=0) {
      speed += jump;
    }
  }

  /*void mouseDragged() {
   float x = mouseX - pmouseX;
   float y = mouseY - pmouseY;
   if ((x>=0 && angleZ <= PI/3) || (x<0 && angleZ >= -PI/3)) {
   angleZ += coeff*x*speed;
   }
   if ((y>=0 && angleX <= PI/3) || (y<0 && angleX >= -PI/3)) {
   angleX += coeff*y*speed;
   }
   }*/

  void mouseDragged(MouseEvent event) {
    float x = event.getX() - width/2;
    float y = event.getY() - height/2;
    float rotateZpercent = abs(x/(width/2));
    float rotateXpercent = abs(y/(height/2));
    float angleZ_updated = rotateZpercent*speed/0.05;
    float angleX_updated = rotateXpercent*speed/0.05;
    //println("angleZ_updated " + angleZ_updated + " angleX_udpated " + angleX_updated);
    //define the four cadrants
    if (x>0 && y>0) {
      angleX = angleX_updated;
      angleZ = angleZ_updated;
    } else if (x<0 && y>0) {
      angleX = angleX_updated;
      angleZ = -angleZ_updated;
    } else if (x<0 && y<0) {
      angleX = -angleX_updated;
      angleZ = -angleZ_updated;
    } else if (x>0 && y<0) {
      angleX = -angleX_updated;
      angleZ = angleZ_updated;
    }
  }

  float getSpeed() {
    return speed;
  }
}


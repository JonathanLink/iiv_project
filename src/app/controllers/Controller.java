package app.controllers;
import app.Parent;
import processing.core.PApplet;
import processing.event.MouseEvent;

abstract class Controller extends Parent {

	public Controller(PApplet parent) {
		super(parent);
	}
	
	public void init() {
	}
	
	abstract public void draw();
	abstract public void update();

	public void keyPressed() {
	}

	public void keyReleased() {
	}

	public void mouseDragged() {
	}

	public void mouseWheel(MouseEvent event) {
	}

	public void mouseClicked() {
	}
	
	public void mousePressed() {
	}
	
	public void setOrigin(float x, float y, float z) {
		p.translate(x, y, z);
	}
	

	

}

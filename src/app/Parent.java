package app;

import processing.core.PApplet;

public abstract class Parent {
	
	protected PApplet p;
	
	public Parent(PApplet parent) {
		this.p = parent;
	}
	
}

package app.views.objects;

import app.controllers.MainController;
import app.views.RenderObject;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

public class Plate extends RenderObject {

	static public final boolean LOAD_3D_PLATE = true;
	static public final int PLATE_HEIGHT = 40;


	public int fillColor;
	public float width;
	public float height;
	public float depth;
	public float speed;

	private PShape tray;
	private PShape wireframeTray;
	
	public Plate(PApplet parent, float width, float depth, float scale, int translateX, int translateY, int translateZ) {
		this(parent,width, Plate.PLATE_HEIGHT, depth, scale, translateX, translateY, translateZ);
	}

	public Plate(PApplet parent, float width, float height, float depth, float scale,int translateX, int translateY, int translateZ) {
		super(parent);
		this.width = width;
		this.height = height;
		this.depth = depth;
		speed = 0.8f;
		fillColor = p.color(192, 192, 192);
		if (LOAD_3D_PLATE) {

			tray = p.loadShape("tray.obj");
			wireframeTray = p.loadShape("wireframeTray.obj");
			
			float rotateY = PApplet.PI/2.0f;
			tray.translate(translateX, translateY, translateZ);
			tray.scale(scale);
			tray.rotateY(rotateY);
			wireframeTray.translate(translateX, translateY, translateZ);
			wireframeTray.scale(scale);
			wireframeTray.rotateY(rotateY);
		
			
		}
	}

	public void updateObject() {
	}

	public void renderObject() {
		
		
		if (LOAD_3D_PLATE) {
			if (angleX > 0.0) {
				//transparent tilted plate to see the ball
				//p.stroke(140.0f, 140.0f, 140.0f);
				p.shape(wireframeTray);
			}  else {
				p.shape(tray);
			}
			
			if (MainController.debug) {
				p.noFill();
				p.color(0,0,0);
				p.strokeWeight(5);
				p.box(this.width, this.height + 20, this.depth);
			}
		} else {
			p.stroke(140.0f, 140.0f, 140.0f);
			if (angleX > 0.0) {
				//transparent tilted plate to see the ball
				p.noFill();
			} else {
				p.fill(fillColor);
			}
			p.box(this.width, this.height, this.depth);
		}
		
		
		
		
		
		if (MainController.debug) drawAxes();
	}

	public void draw2D(PGraphics pGraphics) {
		pGraphics.noStroke();
		pGraphics.fill(fillColor);
		pGraphics.rect(0, 0, this.width, this.depth);
	}


	public String toString() {
		return "I' am the plate!";
	}
}

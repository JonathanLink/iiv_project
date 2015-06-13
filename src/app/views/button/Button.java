package app.views.button;

import app.Parent;
import app.listener.ButtonListener;
import processing.core.PApplet;
import processing.core.PFont;


public class Button extends Parent {
	
	public static final String DEFAULT_FONT = "CHEESEBU.TTF";
	public static final int ROUNDED_EDGE = 10;
	
	public float x;
	public float y;
	public float width;
	public float height;
	public String title;
	public int fillColor;
	public int fontColor;
	public int fontSize;
	public PFont font;
	
	protected ButtonListener listener;
	
	public Button(PApplet parent, float x, float y, float width, int fontSize, String title, ButtonListener listener) {
		super(parent);
		this.fillColor = p.color(255,255,255);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = fontSize * 0.6f;
		this.fontSize = fontSize;
		this.font = p.createFont(DEFAULT_FONT, 170);
		//this.font = p.loadFont(DEFAULT_FONT);
		this.title = title;
		this.listener = listener;
	}
	
	public Button(PApplet parent, float x, float y, float width, String title, ButtonListener listener) {
		this(parent, x, y, width, 20, title, listener);
	}
	
	
	public void draw() {
		
		if (mouseOver()) {
			fillColor = p.color(255,0,0);
			fontColor = p.color(255,255,255);
		} else {
			fillColor = p.color(255,255,255);
			fontColor = p.color(0,0,0);
		}
		
		p.strokeWeight(1);
		p.stroke(0,0,0);
		p.fill(fillColor);
		p.rect(x, y, width, height, ROUNDED_EDGE, ROUNDED_EDGE, ROUNDED_EDGE, ROUNDED_EDGE);
		p.fill(fontColor);
		p.textFont(font);
		p.textSize(fontSize);
		p.textAlign(PApplet.CENTER);
		p.text(title, x + width/2.0f, y + fontSize * 0.6f); 
	}
	
	public void mousePressed() {
		if (mouseOver()) {
			listener.buttonPressed(this);
		}
	}
	
	protected boolean mouseOver()  {
		  return (p.mouseX >= x && p.mouseX <= x +width && 
		      p.mouseY >= y && p.mouseY <= y + height);
	}
	
	

}

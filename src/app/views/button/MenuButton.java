package app.views.button;

import processing.core.PApplet;
import app.listener.ButtonListener;

public class MenuButton extends Button {
	
	private int defaultFontSize;
	
	public MenuButton(PApplet parent, float x, float y, float width, int fontSize, String title, ButtonListener listener) {
		super(parent, x, y, width, fontSize, title, listener);
		this.defaultFontSize = fontSize;
	}

	public void draw() {

		if (mouseOver()) {
			fontColor = p.color(255,255,255);
			fontSize = PApplet.round(defaultFontSize * 1.2f);
		} else {
			fontColor = p.color(255,255,0);
			fontSize = defaultFontSize; 
		}

		p.fill(fontColor);
		p.textFont(font);
		p.textSize(fontSize);
		p.textAlign(PApplet.LEFT);
		p.text(title, x , y + fontSize * 0.6f); 
	}

}

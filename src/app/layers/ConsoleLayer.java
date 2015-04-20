package app.layers;

import java.util.ArrayList;

import processing.core.PApplet;

public class ConsoleLayer extends Layer {

	private ArrayList<String> logs;

	public ConsoleLayer(PApplet parent) {
		super(parent, parent.displayWidth, 150);
		logs = new ArrayList<String>();
	}

	public void write(String log) {
		logs.add(log);
		System.out.println(log);
	}

	protected void drawMyLayer() {
		layer.beginDraw();
		layer.background(0, 0, 0);
		p.fill(255, 255, 255);
		p.textSize(15);
		int y = 10;
		for (int i = logs.size() - 1; i >= 0; i--) {
			String log = logs.get(i); 
			layer.text(log, 0, y, 0);
			y = y + 15;
		}
		layer.endDraw();
	}

	protected int getX() {
		return 0;
	}

	protected int getY() {
		return 0;
	}


}

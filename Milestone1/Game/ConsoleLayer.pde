class ConsoleLayer extends Layer {
  PGraphics consoleSurface;

  ArrayList<String> logs;

  ConsoleLayer() {
    super(displayWidth, 150);
    logs = new ArrayList<String>();
  }

  void drawMyLayer() {
    layer.beginDraw();
    layer.background(0, 0, 0);
    fill(255, 255, 255);
    textSize(15);
    int y = 10;
    for (int i = logs.size() - 1; i >= 0; i--) {
      String log = logs.get(i); 
      layer.text(log, 0, y, 0);
      y = y + 15;
    }
    layer.endDraw();
  }

  int getX() {
    return 0;
  }

  int getY() {
    return 0;
  }

  void write(String log) {
    logs.add(log);
    println(log);
  }
}


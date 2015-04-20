class My2DBox { 

  My2DPoint[] s;
  float alpha = 255.0;
  
  boolean showPoints = false;
  boolean showTexts = false;
  
  My2DBox(My2DPoint[] s) {
    this.s = s; 
  }

  My2DBox(My2DBox box, boolean showPoints, boolean showTexts) {
    this.s = new My2DPoint[box.s.length];
    arrayCopy(box.s, this.s); 
    this.showPoints = showPoints;
    this.showTexts = showTexts;
  }
  
  void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  void render() {
    
 
   strokeWeight(3);
  
    stroke(255, 0, 0, alpha);
    line(s[0].x, s[0].y, s[1].x, s[1].y);
    line(s[1].x, s[1].y, s[2].x, s[2].y);
    line(s[2].x, s[2].y, s[3].x, s[3].y);
    line(s[3].x, s[3].y, s[0].x, s[0].y);

    stroke(0, 255, 0, alpha);
    line(s[4].x, s[4].y, s[5].x, s[5].y);
    line(s[5].x, s[5].y, s[6].x, s[6].y);
    line(s[7].x, s[7].y, s[6].x, s[6].y);
    line(s[4].x, s[4].y, s[7].x, s[7].y);

    stroke(0, 0, 255, alpha);
    line(s[0].x, s[0].y, s[4].x, s[4].y);
    line(s[7].x, s[7].y, s[3].x, s[3].y);
    line(s[2].x, s[2].y, s[6].x, s[6].y);
    line(s[1].x, s[1].y, s[5].x, s[5].y);
  


    // draw points and texts
    for (int i = 0; i < s.length; ++i) {
      fill(255.0, 0.0, 0.0);
      if (showPoints) ellipse(s[i].x, s[i].y, 5, 5);
      textSize(14);
      if (showTexts) text("s[" + i + "]", s[i].x, s[i].y);
    }
  }
}


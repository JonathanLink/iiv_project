package cs211.imageprocessing;

import processing.core.*;

import java.util.ArrayList;
import java.util.Collections; 
import java.util.List;
import java.util.Random; 

public class ImageProcessing extends PApplet{
	
	public static final String BOARD_IMAGE= "board1.jpg"; // <--- HERE to change the image file
	
	PImage img, img2, img3, img4; //orignal images
	PImage imageEdges;
	PImage currentImg;
	float scale = 0.5f;

	float upperBound, lowerBound; //color thresholding with respect of the hue
	float upperBoundSat, lowerBoundSat; //color thresholding with respect of the saturation
	float upperBoundBright, lowerBoundBright; //color thresholding with respect of the brightness

	float discretizationStepsPhi; 
	float discretizationStepsR;
	int[] accumulator;

	// dimensions of the accumulator
	int phiDim; 
	int rDim;
	int numberOfLinesDetected;
	int minVotes;
	int nLines;
	int neighbourhood;

	float[] tabSin;
	float[] tabCos;

	ArrayList<Integer> bestCandidates = new ArrayList<Integer>();
	ArrayList<PVector> detectedLines = new ArrayList<PVector>(); 
	ArrayList<PVector> intersections = new ArrayList<PVector>(); 

	QuadGraph graph = new QuadGraph();
	List<int[]> validQuad = new ArrayList<int[]>();


	//gaussian blur
	float[][] gaussian = { 
	  {
	    9, 12, 9
	  }
	  , 
	  {
	    12, 15, 12
	  }
	  , 
	  {
	    9, 12, 9
	  }
	};

	public void setup() { 

	  //load the original images
	  img = loadImage(BOARD_IMAGE); 
	  /*img2 = loadImage("board2.jpg");
	  img3 = loadImage("board3.jpg");
	  img4 = loadImage("board4.jpg");*/

	  size(ceil(img.width*3*scale), ceil(img.height*scale));

	  //value for the color thresholing
	  lowerBound = 97;
	  upperBound = 137;
	  lowerBoundSat = 52;
	  upperBoundSat = 255;
	  lowerBoundBright = 12;
	  upperBoundBright = 149;

	  //initialize the variables for the lines selection and corner detection
	  numberOfLinesDetected = 0;
	  minVotes = 150;
	  nLines = 4;
	  neighbourhood = 10;
	}

	public void draw() {
	  background(color(255, 255, 255));

	  //Chose the image we want to test
	  currentImg = img;
	  image(currentImg, 0, 0, currentImg.width*scale, currentImg.height*scale);

	  //Detect the edges of the board
	  imageEdges =  boardEdgesDetection(currentImg);
	  
	  //Apply hough algorithm to select lines
	  detectedLines = hough(imageEdges);

	  //Get the corners of the board
	  intersections = getBoardCorner(detectedLines);

	  //Quad detections - uncomment the four following lines to display the valid quads on the first left image
	  /*image(imageEdges, 0, 0, currentImg.width*scale, currentImg.height*scale);
	  graph.build(detectedLines, img.width, img.height);
	  graph.findCycles();
	  filterQuads();*/

	  //Display the accumulator
	  PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
	  for (int i = 0; i < accumulator.length; i++) {
	    houghImg.pixels[i] = color(min(255, 1.5f*accumulator[i]));
	  }
	  houghImg.updatePixels();
	  image(houghImg, currentImg.width*scale, 0, currentImg.width*scale, currentImg.height*scale);

	  //Display the edges image
	  image(imageEdges, currentImg.width*scale*2, 0, currentImg.width*scale, currentImg.height*scale);
	  noLoop();
	}

	public ArrayList<PVector> hough(PImage edgeImg) {

	  //Variable for the computation
	  int accPhi, j;
	  float phi, sinPhi, cosPhi;
	  int accR, k;
	  float r, radius;

	  //Discretization
	  discretizationStepsPhi = 0.06f; 
	  discretizationStepsR = 2.5f;

	  //Dimensions of the accumulator
	  phiDim = (int) (Math.PI / discretizationStepsPhi); 
	  rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR); 

	  //Pre-compute the sin and cos values for optimization
	  tabSin = new float[phiDim]; 
	  tabCos = new float[phiDim];
	  float ang = 0;
	  for (accPhi = 0; accPhi < phiDim; ang += discretizationStepsPhi, accPhi++) {
	    tabSin[accPhi] = (float) Math.sin(ang);
	    tabCos[accPhi] = (float) Math.cos(ang);
	  }

	  //Accumulator with a 1 pix margin around
	  accumulator = new int [(phiDim + 2) * (rDim + 2)];

	  // Fill the accumulator
	  for (int y = 0; y < edgeImg.height; y++) {
	    for (int x = 0; x < edgeImg.width; x++) {

	      //Test if we are on an edge
	      if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
	        for (j = 0; j < phiDim; j++) {
	          radius = x*tabCos[j] + y*tabSin[j]; 
	          k = (int)(round(radius/discretizationStepsR)  + ((rDim-1)/2.0));
	          accumulator[((rDim+2) * (j + 1)) + (k+1)] += 1;
	        }
	      }
	    }
	  }

	  //Store the minVotes best lines
	  for (int idx = 0; idx < accumulator.length; idx++) { 
	    if (accumulator[idx] > minVotes) {
	      numberOfLinesDetected += 1;
	      //Compute back the (r, phi) polar coordinates:
	      accPhi = (int) (idx / (rDim + 2)) - 1;
	      phi = accPhi * discretizationStepsPhi;
	      accR = idx - (accPhi + 1) * (rDim + 2) - 1;
	      r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR; 

	      boolean bestCandidate = true;

	      //Iterate over the neighbourhood
	      for (int dPhi=-neighbourhood/2; dPhi < neighbourhood/2+1; dPhi++) { // check we are not outside the image
	        if ( accPhi+dPhi < 0 || accPhi+dPhi >= phiDim) continue; 
	        for (int dR=-neighbourhood/2; dR < neighbourhood/2 +1; dR++) {// check we are not outside the image
	          if (accR+dR < 0 || accR+dR >= rDim) continue;
	          int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2) + accR + dR + 1;
	          if (accumulator[idx] < accumulator[neighbourIdx]) { // the current idx is not a local maximum!
	            bestCandidate=false;
	            break;
	          }
	        }
	        if (!bestCandidate) break;
	      }

	      if (bestCandidate) {
	        bestCandidates.add(idx);
	      }
	    }
	  }

	  //Sort the best lines
	  Collections.sort(bestCandidates, new HoughComparator(accumulator));

	  //Check on the array size to avoid out-of-bound issue
	  if (bestCandidates.size() < nLines) {
	    nLines = bestCandidates.size();
	  }

	  //Informations in the console
	  println("There are " + numberOfLinesDetected + " lines detected with minVotes = " + minVotes + ".");
	  println("Only " + bestCandidates.size() + " remind after the local maxima selection.");
	  println("Sort the " + bestCandidates.size() + " candidates ...");
	  println("Draw the "+ nLines + " best lines ...");


	  //Draw the lines
	  for (int i = 0; i < nLines; i++) {
	    int idx = bestCandidates.get(i);
	    println("- Line " + (i+1) + " is drawn. It has " + accumulator[idx] + " votes");

	    //Compute back the (r, phi) polar coordinates:
	    accPhi = (int) (idx / (rDim + 2)) - 1;
	    phi = accPhi * discretizationStepsPhi;
	    sinPhi = tabSin[accPhi];
	    cosPhi = tabCos[accPhi];
	    accR = idx - (accPhi + 1) * (rDim + 2) - 1;
	    r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR; 

	    detectedLines.add(new PVector(r, phi));

	    //Compute the intersection of this line with the 4 borders of the image
	    int x0 = 0;
	    int y0 = (int) (r / sinPhi);
	    int x1 = (int) (r / cosPhi);
	    int y1 = 0;
	    int x2 = (int) (edgeImg.width);
	    int y2 = (int) (-cosPhi / sinPhi * x2 + r / sinPhi); 
	    int y3 = (int) (edgeImg.width);
	    int x3 = (int) (-(y3 - r / sinPhi) * (sinPhi / cosPhi));

	    //Plot the lines
	    stroke(204, 102, 0); 
	    if (y0 > 0) {
	      if (x1 > 0)
	        line(x0*scale, y0*scale, x1*scale, y1*scale);
	      else if (y2 > 0)
	        line(x0*scale, y0*scale, x2*scale, y2*scale);
	      else
	        line(x0*scale, y0*scale, x3*scale, y3*scale);
	    } else {
	      if (x1 > 0) {
	        if (y2 > 0)
	          line(x1*scale, y1*scale, x2*scale, y2*scale); 
	        else
	          line(x1*scale, y1*scale, x3*scale, y3*scale);
	      } else
	        line(x2*scale, y2*scale, x3*scale, y3*scale);
	    }
	  }
	  return detectedLines;
	}


	public ArrayList<PVector> getBoardCorner(ArrayList <PVector> lines) { 

	  for (int i = 0; i < lines.size () - 1; i++) {
	    PVector line1 = lines.get(i);
	    for (int j = i + 1; j < lines.size (); j++) {
	      PVector line2 = lines.get(j);  
	      PVector coord = intersection(line1, line2);
	      intersections.add(coord);
	      // draw the intersection
	      fill(255, 128, 0);
	      ellipse(coord.x*scale, coord.y*scale, 8, 8);
	    }
	  }
	  return intersections;
	}



	public PImage boardColorThresholding(PImage img, PImage result) {
	  img.loadPixels();
	  result.loadPixels();

	  for (int i = 0; i < img.width * img.height; i++) {
	    int c = color(img.pixels[i]);
	    if (hue(c) >= lowerBound && hue(c) <= upperBound &&
	      saturation(c) >= lowerBoundSat && saturation(c) <= upperBoundSat &&
	      brightness(c) >= lowerBoundBright && brightness(c) <= upperBoundBright) {
	      result.pixels[i] = color(255);
	    } else {
	      result.pixels[i] = color(0);
	    }
	  }
	  result.updatePixels();
	  return result;
	}


	public PImage convolute(PImage img, PImage result, float[][] kernel, float weight) {

	  img.loadPixels();
	  float sum = 0.f;

	  for (int i = 0; i < img.width*img.height; i++) {
	    result.pixels[i] = color(0);
	  }

	  for (int y = 1; y < img.height-1; y++) { //rows of the images
	    for (int x = 1; x < img.width-1; x++) { //columns of the images

	      sum = 
	        brightness(img.pixels[(y*img.width+x)-img.width - 1])*kernel[0][0] +
	        brightness(img.pixels[(y*img.width+x)-img.width    ])*kernel[0][1] +
	        brightness(img.pixels[(y*img.width+x)-img.width + 1])*kernel[0][2] +
	        brightness(img.pixels[(y*img.width+x)           - 1])*kernel[1][0] +
	        brightness(img.pixels[(y*img.width+x)              ])*kernel[1][1] +
	        brightness(img.pixels[(y*img.width+x)           + 1])*kernel[1][2] +
	        brightness(img.pixels[(y*img.width+x)+img.width - 1])*kernel[2][0] +
	        brightness(img.pixels[(y*img.width+x)+img.width    ])*kernel[2][1] +
	        brightness(img.pixels[(y*img.width+x)+img.width + 1])*kernel[2][2];

	      result.pixels[y*img.width+x] = color(sum/weight);
	    }
	  }

	  result.updatePixels();
	  return result;
	}



	public PImage sobel(PImage img, PImage result) {

	  img.loadPixels();

	  float sum_h = 0.f;
	  float sum_v = 0.f;
	  float sum = 0.f;

	  float[][] hkernel = { 
	    {
	      0, 1, 0
	    }
	    , 
	    {
	      0, 0, 0
	    }
	    , 
	    {
	      0, -1, 0
	    }
	  };


	  float[][] vkernel = { 
	    {
	      0, 0, 0
	    }
	    , 
	    {
	      1, 0, -1
	    }
	    , 
	    {
	      0, 0, 0
	    }
	  };

	  for (int i = 0; i < img.width*img.height; i++) {
	    result.pixels[i] = color(0);
	  }

	  float max = 0;
	  float [] buffer = new float [img.width * img.height];

	  //for each pixels
	  for (int y = 1; y < img.height-1; y++) { //rows of the images
	    for (int x = 1; x < img.width-1; x++) { //columns of the images

	      sum_v = 
	        brightness(img.pixels[(y*img.width+x)-img.width - 1])*vkernel[0][0] +
	        brightness(img.pixels[(y*img.width+x)-img.width    ])*vkernel[0][1] +
	        brightness(img.pixels[(y*img.width+x)-img.width + 1])*vkernel[0][2] +
	        brightness(img.pixels[(y*img.width+x)           - 1])*vkernel[1][0] +
	        brightness(img.pixels[(y*img.width+x)              ])*vkernel[1][1] +
	        brightness(img.pixels[(y*img.width+x)           + 1])*vkernel[1][2] +
	        brightness(img.pixels[(y*img.width+x)+img.width - 1])*vkernel[2][0] +
	        brightness(img.pixels[(y*img.width+x)+img.width    ])*vkernel[2][1] +
	        brightness(img.pixels[(y*img.width+x)+img.width + 1])*vkernel[2][2];

	      sum_h = 
	        brightness(img.pixels[(y*img.width+x)-img.width - 1])*hkernel[0][0] +
	        brightness(img.pixels[(y*img.width+x)-img.width    ])*hkernel[0][1] +
	        brightness(img.pixels[(y*img.width+x)-img.width + 1])*hkernel[0][2] +
	        brightness(img.pixels[(y*img.width+x)           - 1])*hkernel[1][0] +
	        brightness(img.pixels[(y*img.width+x)              ])*hkernel[1][1] +
	        brightness(img.pixels[(y*img.width+x)           + 1])*hkernel[1][2] +
	        brightness(img.pixels[(y*img.width+x)+img.width - 1])*hkernel[2][0] +
	        brightness(img.pixels[(y*img.width+x)+img.width    ])*hkernel[2][1] +
	        brightness(img.pixels[(y*img.width+x)+img.width + 1])*hkernel[2][2];

	      sum = sqrt(pow(sum_h, 2) + pow(sum_v, 2));
	      if (sum > max) {
	        max = sum;
	      }
	      buffer[y*img.width+x] = sum;
	    }
	  }

	  for (int y = 1; y < img.height-1; y++) { //rows of the images
	    for (int x = 1; x < img.width-1; x++) { //columns of the images

	      if (buffer[y*img.width+x] > (int)(max*0.3f)) {
	        result.pixels[y*img.width+x] = color(255);
	      } else {
	        result.pixels[y*img.width+x] = color(0);
	      }
	    }
	  }

	  result.updatePixels();  
	  return result;
	}


	public PImage intensityThresholding(PImage img, PImage result, float threshold) {
	  img.loadPixels();
	  result.loadPixels();
	  for (int i = 0; i < img.width * img.height; i++) {
	    int c = color(img.pixels[i]);
	    if (brightness(c) > threshold) {
	      result.pixels[i] = color (255, 255, 255);
	    } else {
	      result.pixels[i] = color (0, 0, 0);
	    }
	  }
	  result.updatePixels();
	  return result;
	}

	public PImage boardEdgesDetection(PImage img) {

	  //Detect the board with color thresholding
	  PImage boardHSV = boardColorThresholding(img, createImage(img.width, img.height, RGB)); 

	  //Apply gaussian filter three times to blur the image
	  PImage boardBlur = convolute(convolute(convolute(boardHSV, createImage(img.width, img.height, RGB), gaussian, 99), 
	  createImage(img.width, img.height, RGB), gaussian, 99), 
	  createImage(img.width, img.height, RGB), gaussian, 99);

	  //Apply Intensity thresholding
	  PImage boardIntensity = intensityThresholding(boardBlur, createImage(img.width, img.height, RGB), 200);


	  //Apply sobel algorithm to detect the edges
	  PImage boardEdges = sobel(boardIntensity, createImage(img.width, img.height, RGB));


	  return boardEdges;
	}

	public void filterQuads() {
	  int idx = 0;

	  for (int[] quad : graph.cycles) {
	    PVector l1 = detectedLines.get(quad[0]);
	    PVector l2 = detectedLines.get(quad[1]);
	    PVector l3 = detectedLines.get(quad[2]);
	    PVector l4 = detectedLines.get(quad[3]);
	    PVector c12 = intersection(l1, l2);
	    PVector c23 = intersection(l2, l3);
	    PVector c34 = intersection(l3, l4);
	    PVector c41 = intersection(l4, l1);


	    print(idx+1 + " ");

	    //test if the quad is convex, has reasonable size and is non flat  
	    if (graph.isConvex(c12, c23, c34, c41) && 
	      graph.validArea(c12, c23, c34, c41, 600000, 5000) &&
	      graph.nonFlatQuad(c12, c23, c34, c41)) {

	      //add the quad to the valid quad list
	      validQuad.add(quad);
	      Random random = new Random(); 
	      fill(color(min(255, random.nextInt(300)), 
	      min(255, random.nextInt(300)), 
	      min(255, random.nextInt(300)), 50));
	      quad(c12.x*scale, c12.y*scale, c23.x*scale, c23.y*scale, c34.x*scale, c34.y*scale, c41.x*scale, c41.y*scale);
	      println("Quad at (" + c12.x + "," +  c12.y + ") / (" + c23.x + "," + c23.y + ") / (" + c34.x + "," + c34.y + ") / (" + c41.x + "," + c41.y + ")");
	      idx ++;
	    }
	  }
	  println("At the end of the quad selection, there are " + validQuad.size() + " quad(s) that are valid.");
	}


	//method that returns the (x,y) coordinates of the intersection between two lines, display the intersection as well
	public PVector intersection(PVector line1, PVector line2) {

	  double sin_t1 = Math.sin(line1.y);
	  double sin_t2 = Math.sin(line2.y);
	  double cos_t1 = Math.cos(line1.y);
	  double cos_t2 = Math.cos(line2.y);
	  float r1 = line1.x;
	  float r2 = line2.x;

	  double denom = cos_t2 * sin_t1 - cos_t1 * sin_t2;

	  int x = (int) ((r2 * sin_t1 - r1 * sin_t2) / denom);
	  int y = (int) ((-r2 * cos_t1 + r1 * cos_t2) / denom);
	  return (new PVector(x, y));
	}


}

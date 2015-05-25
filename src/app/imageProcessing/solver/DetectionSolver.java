package app.imageProcessing.solver;


import app.Parent;
import app.imageProcessing.comparator.CWComparator;
import app.imageProcessing.comparator.HoughComparator;
import processing.core.*;

import java.util.*;


public class DetectionSolver extends Parent{

	public static float scale = 0.35f;

	//variable for lines detections
	private float discretizationStepsPhi; 
	private float discretizationStepsR;
	private int[] accumulator;
	private int phiDim; // dimensions of the accumulator
	private int rDim; 	// dimensions of the accumulator
	private int numberOfLinesDetected;
	private int minVotes;
	private int nLines;
	private int neighbourhood;
	private float[] tabSin;
	private float[] tabCos;
	private List<Integer> bestCandidates = new ArrayList<Integer>();
	private List<PVector> detectedLines = new ArrayList<PVector>(); 

	//variable for corners detection
	private QuadGraph graph = new QuadGraph();
	private List<int[]> validQuad = new ArrayList<int[]>();
	private List<List<PVector>> quadsCoordinates = new ArrayList<List<PVector>>(); 
	private List<PVector> fourCorners = new ArrayList<PVector>(); 
	private PGraphics resultImage;

	public DetectionSolver(PApplet parent, PGraphics resultImage) {
		super(parent);
		minVotes = 100;
		nLines = 4;
		neighbourhood = 15;
		this.resultImage = resultImage;
	}

	public List<PVector> solve(PImage img) {
		//Apply hough algorithm to select lines
		//System.out.println("******************************************************************************************************************************");
		detectedLines = hough(img);

		//Get the corners coordinates of the board
		if (detectedLines.size() < 4) {
			System.err.println("There is not enough detected lines");
		} else {
			//System.out.println("******************************************************************************************************************************");
			graph.build(detectedLines, img.width, img.height);
			graph.findCycles();
			filterQuads();
			if(validQuad.size() < 1 ) {
				System.err.println("There is no quad detected");
			} else {
				//System.out.println("******************************************************************************************************************************");
				for (int i = 0; i < validQuad.size(); i ++) {
					quadsCoordinates.set(i,sortCorners(quadsCoordinates.get(i)));
				}
				selectTheBestQuad();
			}	
		}
		return fourCorners;
	}

	public void selectTheBestQuad() {

		PVector c0;
		PVector c1;
		PVector c2;
		PVector c3;
		PVector diag1;
		PVector diag2;

		int best = 0;
		float minDist = 10000000;

		for (int i = 0; i < validQuad.size(); i ++) {
			c0 = (quadsCoordinates.get(i)).get(0);
			c1 = (quadsCoordinates.get(i)).get(1);
			c2 = (quadsCoordinates.get(i)).get(2);
			c3 = (quadsCoordinates.get(i)).get(3);
			diag1 = PVector.sub(c0, c2);
			diag2 = PVector.sub(c1, c3);

			if (Math.abs(diag1.mag() - diag2.mag()) < minDist) {
				//best = i;
				minDist = Math.abs(diag1.mag() - diag2.mag());
			}
		}

		fourCorners.add((quadsCoordinates.get(best)).get(0));
		fourCorners.add((quadsCoordinates.get(best)).get(1));
		fourCorners.add((quadsCoordinates.get(best)).get(2));
		fourCorners.add((quadsCoordinates.get(best)).get(3));
		System.out.println("Final board at " + Arrays.toString(fourCorners.toArray()));

		resultImage.beginDraw();
		resultImage.fill(255, 0, 0);
		resultImage.ellipse(fourCorners.get(0).x*scale, fourCorners.get(0).y*scale, 8,8);
		resultImage.ellipse(fourCorners.get(1).x*scale, fourCorners.get(1).y*scale, 8,8);
		resultImage.ellipse(fourCorners.get(2).x*scale, fourCorners.get(2).y*scale, 8,8);
		resultImage.ellipse(fourCorners.get(3).x*scale, fourCorners.get(3).y*scale, 8,8);
		resultImage.endDraw();
	}

	public List<PVector> hough(PImage edgeImg) {

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
				if (p.brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
					for (j = 0; j < phiDim; j++) {
						radius = x*tabCos[j] + y*tabSin[j]; 
						k = (int)(PApplet.round(radius/discretizationStepsR)  + ((rDim-1)/2.0));
						accumulator[((rDim+2) * (j + 1)) + (k+1)] += 1;
					}
				}
			}
		}

		System.out.println("There are " + numberOfLinesDetected + " lines detected with minVotes = " + minVotes + ".");

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

		//System.out.println("Only " + bestCandidates.size() + " remind after the local maxima selection.");

		//Sort the best lines
		Collections.sort(bestCandidates, new HoughComparator(accumulator));
		//System.out.println("Sort the " + bestCandidates.size() + " candidates ...");


		//Check on the array size to avoid out-of-bound issue
		if (bestCandidates.size() < nLines) {
			nLines = bestCandidates.size();
			System.out.println("Draw the "+ nLines + " best lines ...");

		}
		int bound = 0;
		if(bestCandidates.size() >= 6) {
			bound = 6;		
		}

		//Draw the lines
		for (int i = 0; i < bound; i++) {
			int idx = bestCandidates.get(i);
			//System.out.println("- Line " + (i+1) + " is drawn. It has " + accumulator[idx] + " votes");

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
			/*resultImage.beginDraw();
			resultImage.stroke(204, 102, 0); 
			if (y0 > 0) {
				if (x1 > 0)
					resultImage.line(x0*scale, y0*scale, x1*scale, y1*scale);
				else if (y2 > 0)
					resultImage.line(x0*scale, y0*scale, x2*scale, y2*scale);
				else
					resultImage.line(x0*scale, y0*scale, x3*scale, y3*scale);
			} else {
				if (x1 > 0) {
					if (y2 > 0)
						resultImage.line(x1*scale, y1*scale, x2*scale, y2*scale); 
					else
						resultImage.line(x1*scale, y1*scale, x3*scale, y3*scale);
				} else
					resultImage.line(x2*scale, y2*scale, x3*scale, y3*scale);
			}
			resultImage.endDraw();*/
		}

		return detectedLines;

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

			//test if the quad is convex, has reasonable size and is rectangular 
			if (graph.isConvex(c12, c23, c34, c41)) {
				if (graph.validArea(c12, c23, c34, c41, 600000, 40000) &&
						graph.isFlat(c12, c23, c34, c41)) {

					//add the quad to the valid quad list
					validQuad.add(quad);

					//draw the quad
					/*resultImage.beginDraw();
					Random random = new Random(); 
					resultImage.fill(p.color(PApplet.min(255, random.nextInt(300)), 
							PApplet.min(255, random.nextInt(300)), 
							PApplet.min(255, random.nextInt(300)), 50));
					resultImage.quad(c12.x*scale, c12.y*scale, c23.x*scale, c23.y*scale, c34.x*scale, c34.y*scale, c41.x*scale, c41.y*scale);*/

					//add the coordinates of the current quad
					quadsCoordinates.add(idx,Arrays.asList(c12,c23,c34,c41));
					idx++;

					// draw the corners
					/*resultImage.fill(255, 128, 0);
					resultImage.text("(" + c12.x + "," + c12.y + ")", c12.x*scale,c12.y*scale);
					resultImage.ellipse(c12.x*scale, c12.y*scale, 5,5);
					resultImage.text("(" + c23.x + "," + c23.y + ")", c23.x*scale,c23.y*scale);
					resultImage.ellipse(c23.x*scale, c23.y*scale, 5,5);
					resultImage.text("(" + c34.x + "," + c34.y + ")", c34.x*scale,c34.y*scale);
					resultImage.ellipse(c34.x*scale, c34.y*scale, 5,5);
					resultImage.text("(" + c41.x + "," + c41.y + ")", c41.x*scale,c41.y*scale);
					resultImage.ellipse(c41.x*scale, c41.y*scale, 5,5);

					resultImage.endDraw();*/
				}
			}
		}
		System.out.println("At the end of the quad selection, there are " + validQuad.size() + " quad(s) that are valid.");

	}

	public List<PVector> sortCorners(List<PVector> quad) {
		// Sort corners so that they are ordered clockwise
		PVector a = quad.get(0);
		PVector b = quad.get(2);

		PVector center = new PVector((a.x+b.x)/2,(a.y+b.y)/2);
		Collections.sort(quad,new CWComparator(center));
		System.out.println("Sorting the coordinates clockwise...");

		// Re-order the corners so that the first one is the closest to the
		// origin (0,0) of the image.

		float minDistance = 100000;
		float distanceToOrigin; 
		int idx = 0;

		for (int i = 0; i < 4; i++) {
			PVector dist = PVector.sub(quad.get(i),new PVector(0,0));
			distanceToOrigin = dist.mag();
			System.out.println("Distance between center and corner " + i + ": " + distanceToOrigin);
			if(distanceToOrigin < minDistance) {
				minDistance = distanceToOrigin;
				idx = i;
			}
		}

		Collections.rotate(quad,idx);
		System.out.println("Sorting the coordinates with closest to origin");
		System.out.println("Board at 0(" + quad.get(0).x + "," +  quad.get(0).y + ") / 1(" + quad.get(1).x + "," +  quad.get(1).y + ") / 2(" + quad.get(2).x + "," +  quad.get(2).y + ") / 3(" + quad.get(3).x + "," +  quad.get(3).y + ")");
		return quad; 
	}

	//General methods which returns the (x,y) coordinates of the intersection between two lines
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

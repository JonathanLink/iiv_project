package app.imageProcessing.solver;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import app.Parent;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class BlobDetection extends Parent {
	Polygon quad = new Polygon();
	
	private List<PVector> blobCenters; 
	private int lengthBoardSize;
	private int topBoardPosition;
	private int leftBoardPosition;
	private final int MARGIN = 20;
	

	/** Create a blob detection instance with the four corners of the Lego board.
	 */
	public BlobDetection(PApplet parent, PVector c1, PVector c2, PVector c3, PVector c4) {
		super(parent);
	
		quad.addPoint((int) c1.x + MARGIN , (int) c1.y + MARGIN);
		quad.addPoint((int) c2.x - MARGIN, (int) c2.y + MARGIN);
		quad.addPoint((int) c3.x - MARGIN, (int) c3.y - MARGIN);
		quad.addPoint((int) c4.x + MARGIN, (int) c4.y - MARGIN); 
		
		this.blobCenters = new ArrayList<PVector>();
		this.lengthBoardSize = (int) PVector.dist(c1, c2);
		this.topBoardPosition = (int) c1.y;
		this.leftBoardPosition = (int) c1.x;
	}
	
	public List<PVector> getBlobCenters() {
		return this.blobCenters;
	}
	
	public int getLengthBoardSize() {
		return lengthBoardSize;
	}
	
	public int getTopBoardPosition() {
		return topBoardPosition;
	}
	
	public int getLeftBoardPosition() {
		return leftBoardPosition;
	}

	/** Returns true if a (x,y) point lies inside the quad
	 */
	public boolean isInQuad(int x, int y) {
		return quad.contains(x, y);
	}

	public PImage colorThresholding(PImage img, int lowerBoundHue, int upperBoundHue) {
		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.RGB);

		for (int i = 0; i < img.width * img.height; i++) {

			//precompute the values before testing the condition
			int c = p.color(img.pixels[i]);
			float hue = p.hue(c);

			if (hue >= lowerBoundHue && hue <= upperBoundHue) {
				result.pixels[i] = p.color(255);
			} else {
				result.pixels[i] = p.color(0);
			}
		}

		result.updatePixels();
		return result;
	}

	public PImage findConnectedComponents(PImage input){

		// First pass: label the pixels and store labels' equivalences
		int [] labels= new int [input.width*input.height];
		List<TreeSet<Integer>> labelsEquivalences= new ArrayList<TreeSet<Integer>>();
		int currentLabel = -1;
		currentLabel = createANewLabel(currentLabel, labelsEquivalences); 

		for (int x = 0; x < input.width; x++) {
			for (int y = 0; y < input.height; y++) {
				if (isInQuad(x, y)) {
					if (!pixelOnEdge(x, y, input.width, input.height)) {
						if (isPixelWhite(x, y, input)) {
							List<Integer> neighboursLabels = getNeighbours(x, y, labels, input.width);
							if (areAllLabelsZero(neighboursLabels)) {
								currentLabel = createANewLabel(currentLabel, labelsEquivalences);
								setLabelForCoord(x, y, currentLabel, labels, input.width);
							} else {
								if (allNeighboursHaveSameLabel(neighboursLabels)) {
									int labelValue = neighboursLabels.get(0);
									setLabelForCoord(x, y, labelValue, labels, input.width);
								} else {
									int smallestLabel = getSmallestLabelAmongNeighbours(neighboursLabels);
									setLabelForCoord(x, y, smallestLabel, labels, input.width);
									updateEquivalentTable(neighboursLabels, smallestLabel, labelsEquivalences);
								}
							}
						}
					}
				}
			}
		}

		// Second pass: re-label the pixels by their equivalent class
		for (int x = 0; x < input.width; x++) {
			for (int y = 0; y < input.height; y++) {
				if (isInQuad(x, y)) {
					if (!pixelOnEdge(x, y, input.width, input.height)) {
						if (isPixelWhite(x, y, input)) {
							replaceIdWithSmallestEquivalentOne(x, y, labels, input.width, labelsEquivalences);
						}
					}
				}
			}
		}

		// Finally, output an image with each blob colored in one uniform color.
		List<Integer> filterLabels = filterBlobWithMinimumSize(labels, 100);
		HashMap<Integer, Integer> colors = generateBlobColors(filterLabels);
		
		PImage result = p.createImage(input.width, input.height, PApplet.RGB);
		for (int x = 0; x < input.width; x++) {
			for (int y = 0; y < input.height; y++) {
				if (isInQuad(x, y)) {
					if (!pixelOnEdge(x, y, input.width, input.height)) {
						int index = getIndexForCoord(x, y, input.width);
						int label = labels[index];
						if (filterLabels.contains(label)) { // only display blob we have filtered (i.e the one whose id is in the filter list)
							result.pixels[index] = colors.get(label);
						}
					}
				}
			}
		}

		result.updatePixels();
		
		// last step, compute the centroid of the blobs
		generateBlobCenterList(labels, filterLabels, input.width, input.height);
		
		return result;

	}


	private boolean isPixelWhite(int x, int y, PImage image) {
		int index = x + y * image.width;
		int color = p.color(image.pixels[index]);
		return color == p.color(255,255,255);
	}

	private boolean pixelOnEdge(int x, int y, int width, int height) {
		return (x == 0 || x == width || y == 0 || y == height);
	}

	private List<Integer> getNeighbours(int x, int y, int[] labels, int imageWidth) {
		ArrayList<Integer> neighbours = new ArrayList<Integer>();

		neighbours.add(getLabelForCoord(x-1, y-1, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x, y-1, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x+1, y-1, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x-1, y, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x+1, y, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x-1, y+1, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x, y+1, labels, imageWidth ));
		neighbours.add(getLabelForCoord(x+1, y+1, labels, imageWidth ));

		return neighbours;
	}

	private TreeSet<Integer> transformNeighboursIdFromListToSet( List<Integer> neighbours) {
		return new TreeSet<Integer>(neighbours);
	}

	private int getLabelForCoord(int x, int y, int[] labels, int imageWidth) {
		int index = getIndexForCoord(x, y, imageWidth);
		return labels[index];
	}

	private void setLabelForCoord(int x, int y, int value, int[] labels, int imageWidth) {
		int index = getIndexForCoord(x, y, imageWidth);
		labels[index] = value;
	}

	private int getIndexForCoord(int x, int y, int imageWidth) {
		return x + (y * imageWidth);
	}

	private boolean allNeighboursHaveSameLabel(List<Integer> neighboursLabel) {
		int lastLabel = neighboursLabel.get(0); // 
		for (int i = 1; i < neighboursLabel.size(); ++i) {
			if (neighboursLabel.get(i) != lastLabel) {
				return false;
			}
		}
		return true;
	}

	private int getSmallestLabelAmongNeighbours(List<Integer> neighboursLabel) {
		int smallestLabel = Integer.MAX_VALUE; 
		for (int i = 0; i < neighboursLabel.size(); ++i) {
			if (neighboursLabel.get(i) > 0 && neighboursLabel.get(i) < smallestLabel) {
				smallestLabel = neighboursLabel.get(i);
			}
		}
		return smallestLabel;
	}

	private void updateEquivalentTable(List<Integer> neighboursLabel, int smallestLabel, List<TreeSet<Integer>> labelsEquivalences) {
		TreeSet<Integer> neighboursSetIds = transformNeighboursIdFromListToSet(neighboursLabel);
		neighboursSetIds.remove(0);// we don't want any references to the 0 id in the labelEquivalences list. Id start with number 1!
		Iterator<Integer> iterator = neighboursSetIds.iterator();
		while (iterator.hasNext()) {
			int id = (int) iterator.next();
			labelsEquivalences.get(id).addAll(neighboursSetIds);
		}

	}


	private boolean areAllLabelsZero(List<Integer> neighboursLabel) {
		for (int i = 0; i < neighboursLabel.size(); ++i) {
			if (neighboursLabel.get(i) != 0) {
				return false;
			}
		}
		return true;
	}

	private int createANewLabel(int currentLabel, List<TreeSet<Integer>> labelsEquivalences) {
		int newLabel = currentLabel + 1;
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(newLabel);
		labelsEquivalences.add(set);
		return newLabel;
	}

	private void replaceIdWithSmallestEquivalentOne(int x, int y, int[] labels, int width,  List<TreeSet<Integer>> labelsEquivalences) {
		int label = getLabelForCoord(x, y, labels, width);
		int smallestEquivalent = labelsEquivalences.get(label).first();
		setLabelForCoord(x, y, smallestEquivalent, labels, width);
	}
	
	private void displayBlobStats(HashMap<Integer, Integer> blobs) {
		Iterator<Entry<Integer, Integer>> iterator = blobs.entrySet().iterator();
		//System.out.println("\nBlob ID | # of pixels for this blob");
		//System.out.println("======= | ============================");
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>)iterator.next();
			//System.out.println(pair.getKey() + " | " + pair.getValue());
		}
	}
	
	private List<Integer> filterBlobWithMinimumSize(int[] labels, int minimumSize) {
		HashMap<Integer, Integer> blobs = new HashMap<Integer, Integer>(); // <blob id, counter>
		for (int i = 0; i < labels.length; ++i) {
			int key = labels[i];
			int value = (blobs.get(key) != null)? blobs.get(key) + 1 : 1;
			blobs.put(key, value); 
		}
		
		displayBlobStats(blobs); // log for debug:
		
		//System.out.println("\nOnly keep blob with a minimum size of: " + minimumSize);
		Iterator<Entry<Integer, Integer>> iterator = blobs.entrySet().iterator();
		List<Integer> filterBlobIds = new ArrayList<Integer>();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>)iterator.next();
			if (pair.getValue() >= minimumSize) {
				filterBlobIds.add(pair.getKey());
			}
		}
		
		return filterBlobIds;
		
		
	}
	
	private HashMap<Integer, Integer> generateBlobColors(List<Integer> blobIds) {
		HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>(); // <blob id, color>
		for (int i = 0; i < blobIds.size(); ++i) {
			if (blobIds.get(i) == 0) {
				colors.put(0, p.color(0,0,0));
			} else {
				colors.put(blobIds.get(i), p.color(p.random(255), p.random(255), p.random(255)));
			}
		}
		return colors;
	}
	
	private void generateBlobCenterList(int[] labels, List<Integer> filterBlobIds, int imageWidth, int imageHeight) {
		this.blobCenters.clear(); // first reset the list
		
		HashMap<Integer, PVector> blobsCenter = new HashMap<Integer, PVector>(); // <blob id, center coord >
		HashMap<Integer, Integer> blobsCounter = new HashMap<Integer, Integer>(); // <blob id, counter>
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				int index = getIndexForCoord(x, y, imageWidth);
				int label = labels[index];
				if (filterBlobIds.contains(label) && label != 0) { // again id 0 we don't care. It does not correspond to a blob id. It is the id of the black background.
					// add coordinate
					//PVector coordinate = new PVector(x,y);
					PVector coordinate = new PVector(x,y);
					PVector plateOrigin = new PVector(leftBoardPosition, topBoardPosition);
					coordinate = PVector.sub(coordinate, plateOrigin);
					if (blobsCenter.get(label) != null) {
						coordinate = PVector.add(blobsCenter.get(label), coordinate);
					}
					blobsCenter.put(label, coordinate);
					
					// add 1 to counter
					int value = (blobsCounter.get(label) != null)? blobsCounter.get(label) + 1 : 1;
					blobsCounter.put(label, value); 
				}
			}
		}
		
		// Compute the avergage of the coordinate
		Iterator<Entry<Integer, PVector>> iterator = blobsCenter.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, PVector> blob = (Map.Entry<Integer, PVector>)iterator.next();
			blob.setValue( PVector.div(blob.getValue() , blobsCounter.get(blob.getKey())) );
		}
		
		this.blobCenters.addAll(blobsCenter.values());
		
	}


}

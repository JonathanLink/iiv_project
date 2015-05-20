package app.imageProcessing.solver;


import app.Parent;
import processing.core.*;

public class ImageProcessingSolver extends Parent {

	public static float scale = 0.7f;

	//gaussian filter
	private static final float[][] gaussian = { 
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

	//thresholds
	public float upperBoundHue, lowerBoundHue; 
	public float upperBoundSat, lowerBoundSat; 
	public float upperBoundBright, lowerBoundBright; 

	public ImageProcessingSolver(PApplet parent) {
		super(parent);

		//threshold for board1/2/3/4.jpg
		/*lowerBoundHue = 97;
		upperBoundHue = 137;
		lowerBoundSat = 52;
		upperBoundSat = 255;
		lowerBoundBright = 12;
		upperBoundBright = 149;*/

		//threshold for 1/2/3/4.png
		/*lowerBoundHue = 64;
		upperBoundHue = 127;
		lowerBoundSat = 41;
		upperBoundSat = 255;
		lowerBoundBright = 46;
		upperBoundBright = 150;*/

		//threshold for interaction mode - to tune each time
		/*lowerBoundHue = 59;
		upperBoundHue = 106;
		lowerBoundSat = 78;
		upperBoundSat =231;
		lowerBoundBright = 19;
		upperBoundBright = 162;*/
		
		lowerBoundHue = 79;
		upperBoundHue = 128;
		lowerBoundSat = 50;
		upperBoundSat = 255;
		lowerBoundBright = 49;
		upperBoundBright = 247;

	}

	public PImage solve(PImage img) {
		int c = p.color(255, 255, 255);
		p.background(c);
		PImage imageEdges =  pipelineToSobel(img);
		return imageEdges;
	}


	public PImage colorThresholding(PImage img) {
		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.RGB);

		for (int i = 0; i < img.width * img.height; i++) {
			int c = p.color(img.pixels[i]);
			if (p.hue(c) >= lowerBoundHue && p.hue(c) <= upperBoundHue &&
					p.saturation(c) >= lowerBoundSat && p.saturation(c) <= upperBoundSat &&
					p.brightness(c) >= lowerBoundBright && p.brightness(c) <= upperBoundBright) {
				result.pixels[i] = p.color(255);
			} else {
				result.pixels[i] = p.color(0);
			}
		}
		result.updatePixels();
		return result;
	}

	public PImage convolute(PImage img, float[][] kernel, float weight) {

		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.RGB);

		float sum = 0.f;

		for (int i = 0; i < img.width*img.height; i++) {
			result.pixels[i] = p.color(0);
		}

		for (int y = 1; y < img.height-1; y++) { //rows of the images
			for (int x = 1; x < img.width-1; x++) { //columns of the images

				sum = 
						p.brightness(img.pixels[(y*img.width+x)-img.width - 1])*kernel[0][0] +
						p.brightness(img.pixels[(y*img.width+x)-img.width    ])*kernel[0][1] +
						p.brightness(img.pixels[(y*img.width+x)-img.width + 1])*kernel[0][2] +
						p.brightness(img.pixels[(y*img.width+x)           - 1])*kernel[1][0] +
						p.brightness(img.pixels[(y*img.width+x)              ])*kernel[1][1] +
						p.brightness(img.pixels[(y*img.width+x)           + 1])*kernel[1][2] +
						p.brightness(img.pixels[(y*img.width+x)+img.width - 1])*kernel[2][0] +
						p.brightness(img.pixels[(y*img.width+x)+img.width    ])*kernel[2][1] +
						p.brightness(img.pixels[(y*img.width+x)+img.width + 1])*kernel[2][2];

				result.pixels[y*img.width+x] = p.color(sum/weight);
			}
		}

		result.updatePixels();
		return result;
	}


	public PImage intensityThresholding(PImage img, float threshold) {
		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.RGB);

		for (int i = 0; i < img.width * img.height; i++) {
			int c = p.color(img.pixels[i]);
			if (p.brightness(c) > threshold) {
				result.pixels[i] = p.color (255, 255, 255);
			} else {
				result.pixels[i] = p.color (0, 0, 0);
			}
		}
		result.updatePixels();
		return result;
	}

	public PImage sobel(PImage img) {

		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.RGB);

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
			result.pixels[i] = p.color(0);
		}

		float max = 0;
		float [] buffer = new float [img.width * img.height];

		//for each pixels
		for (int y = 1; y < img.height-1; y++) { //rows of the images
			for (int x = 1; x < img.width-1; x++) { //columns of the images

				sum_v = 
						p.brightness(img.pixels[(y*img.width+x)-img.width - 1])*vkernel[0][0] +
						p.brightness(img.pixels[(y*img.width+x)-img.width    ])*vkernel[0][1] +
						p.brightness(img.pixels[(y*img.width+x)-img.width + 1])*vkernel[0][2] +
						p.brightness(img.pixels[(y*img.width+x)           - 1])*vkernel[1][0] +
						p.brightness(img.pixels[(y*img.width+x)              ])*vkernel[1][1] +
						p.brightness(img.pixels[(y*img.width+x)           + 1])*vkernel[1][2] +
						p.brightness(img.pixels[(y*img.width+x)+img.width - 1])*vkernel[2][0] +
						p.brightness(img.pixels[(y*img.width+x)+img.width    ])*vkernel[2][1] +
						p.brightness(img.pixels[(y*img.width+x)+img.width + 1])*vkernel[2][2];

				sum_h = 
						p.brightness(img.pixels[(y*img.width+x)-img.width - 1])*hkernel[0][0] +
						p.brightness(img.pixels[(y*img.width+x)-img.width    ])*hkernel[0][1] +
						p.brightness(img.pixels[(y*img.width+x)-img.width + 1])*hkernel[0][2] +
						p.brightness(img.pixels[(y*img.width+x)           - 1])*hkernel[1][0] +
						p.brightness(img.pixels[(y*img.width+x)              ])*hkernel[1][1] +
						p.brightness(img.pixels[(y*img.width+x)           + 1])*hkernel[1][2] +
						p.brightness(img.pixels[(y*img.width+x)+img.width - 1])*hkernel[2][0] +
						p.brightness(img.pixels[(y*img.width+x)+img.width    ])*hkernel[2][1] +
						p.brightness(img.pixels[(y*img.width+x)+img.width + 1])*hkernel[2][2];

				sum = PApplet.sqrt(PApplet.pow(sum_h, 2) + PApplet.pow(sum_v, 2));
				if (sum > max) {
					max = sum;
				}
				buffer[y*img.width+x] = sum;
			}
		}

		for (int y = 1; y < img.height-1; y++) { //rows of the images
			for (int x = 1; x < img.width-1; x++) { //columns of the images

				if (buffer[y*img.width+x] > (int)(max*0.3f)) {
					result.pixels[y*img.width+x] = p.color(255);
				} else {
					result.pixels[y*img.width+x] = p.color(0);
				}
			}
		}

		result.updatePixels();  
		return result;
	}

	public PImage pipelineToSobel(PImage img) {

		//Detect the board with color thresholding
		PImage boardHSV = colorThresholding(img); 

		//Apply gaussian filter three times to blur the image
		PImage boardBlur = convolute(convolute(convolute(boardHSV, gaussian, 99), gaussian, 99), gaussian, 99);

		//Apply Intensity thresholding
		PImage boardIntensity = intensityThresholding(boardBlur, 200);

		//Apply sobel algorithm to detect the edges
		PImage boardEdges = sobel(boardIntensity);
		return boardEdges;
	}

}

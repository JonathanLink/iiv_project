package app.imageProcessing.solver;


import app.Parent;
import app.controllers.PlateController;
import processing.core.*;

public class ImageProcessingSolver extends Parent {

	private float scale = 0.35f;

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

	public ImageProcessingSolver(PApplet parent, float scale) {
		super(parent);
		this.scale = scale;

		//threshold for fixed images board1/2/3/4.jpg
		/*lowerBoundHue = 97;
		upperBoundHue = 137;
		lowerBoundSat = 52;
		upperBoundSat = 255;
		lowerBoundBright = 12;
		upperBoundBright = 149;*/


		//threshold for the video 
		if(PlateController.DEMO) {
			lowerBoundHue = 108;
			upperBoundHue = 127;
			lowerBoundSat = 50;
			upperBoundSat =255;
			lowerBoundBright = 88;
			upperBoundBright = 255;
		} else {
			//TODO IF YOU WANT TO USE THE WEBCAM, DO A CALIBRATION TO HAVE THE CORRECT THRESHOLDS
			//we can provide you 'Calibration.pde' to find the correct threshold, send e-mail to celine.dupuis@epfl.ch
			/*lowerBoundHue = 97;
			upperBoundHue = 137;
			lowerBoundSat = 52;
			upperBoundSat = 255;
			lowerBoundBright = 12;
			upperBoundBright = 149;*/

		}

	}

	public PImage solve(PImage img) {
		int c = p.color(255, 255, 255);
		p.background(c);
		PImage imageEdges =  pipelineToSobel(img);
		return imageEdges;
	}


	public PImage colorThresholding(PImage img) {
		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.ALPHA);

		for (int i = 0; i < img.width * img.height; i++) {

			//precompute the values before testing the condition
			int c = p.color(img.pixels[i]);
			float sat = p.saturation(c);
			float hue = p.hue(c);
			float bright = p.brightness(c);

			if (hue >= lowerBoundHue && hue <= upperBoundHue &&
					sat >= lowerBoundSat && sat <= upperBoundSat &&
					bright >= lowerBoundBright && bright <= upperBoundBright) {
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
		PImage result = p.createImage(img.width, img.height, PApplet.ALPHA);
		float sum = 0.f;

		for (int y = 1; y < img.height-1; y++) { //rows of the images
			for (int x = 1; x < img.width-1; x++) { //columns of the images

				//precompute the current value
				int current = (y*img.width+x);

				/*sum = 
						p.brightness(img.pixels[current-img.width - 1])*kernel[0][0] +
						p.brightness(img.pixels[current-img.width    ])*kernel[0][1] +
						p.brightness(img.pixels[current-img.width + 1])*kernel[0][2] +
						p.brightness(img.pixels[current           - 1])*kernel[1][0] +
						p.brightness(img.pixels[current              ])*kernel[1][1] +
						p.brightness(img.pixels[current           + 1])*kernel[1][2] +
						p.brightness(img.pixels[current+img.width - 1])*kernel[2][0] +
						p.brightness(img.pixels[current+img.width    ])*kernel[2][1] +
						p.brightness(img.pixels[current+img.width + 1])*kernel[2][2];*/

				//Optimisation: avoid the kernel array search
				sum = 
						p.brightness(img.pixels[current-img.width - 1])*9 +
						p.brightness(img.pixels[current-img.width    ])*12 +
						p.brightness(img.pixels[current-img.width + 1])*9 +
						p.brightness(img.pixels[current           - 1])*12 +
						p.brightness(img.pixels[current              ])*15 +
						p.brightness(img.pixels[current           + 1])*12 +
						p.brightness(img.pixels[current+img.width - 1])*9 +
						p.brightness(img.pixels[current+img.width    ])*12 +
						p.brightness(img.pixels[current+img.width + 1])*9;

				result.pixels[current] = p.color(sum/weight);			
			}
		}
		result.updatePixels();
		return result;
	}


	public PImage intensityThresholding(PImage img, float threshold) {
		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.ALPHA);

		for (int i = 0; i < img.width * img.height; i++) {
			int c = p.color(img.pixels[i]);
			if (p.brightness(c) > threshold) {
				result.pixels[i] = p.color (255);
			} else {
				result.pixels[i] = p.color (0);
			}
		}
		result.updatePixels();
		return result;
	}

	public PImage sobel(PImage img) {

		img.loadPixels();
		PImage result = p.createImage(img.width, img.height, PApplet.ALPHA);

		float sum_h = 0.f;
		float sum_v = 0.f;
		float sum = 0.f;

		/*float[][] hkernel = { 
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
		};*/

		/*for (int i = 0; i < img.width*img.height; i++) {
			result.pixels[i] = p.color(0);
		}*/

		//for each pixels
		for (int y = 1; y < img.height-1; y++) { //rows of the images
			for (int x = 1; x < img.width-1; x++) { //columns of the images
				int current = (y*img.width+x);
				/*sum_v = 
						p.brightness(img.pixels[current-img.width - 1])*vkernel[0][0] +
						p.brightness(img.pixels[current-img.width    ])*vkernel[0][1] +
						p.brightness(img.pixels[current-img.width + 1])*vkernel[0][2] +
						p.brightness(img.pixels[current          - 1])*vkernel[1][0] +
						p.brightness(img.pixels[current              ])*vkernel[1][1] +
						p.brightness(img.pixels[current           + 1])*vkernel[1][2] +
						p.brightness(img.pixels[current+img.width - 1])*vkernel[2][0] +
						p.brightness(img.pixels[current+img.width    ])*vkernel[2][1] +
						p.brightness(img.pixels[current+img.width + 1])*vkernel[2][2];

				sum_h = 
						p.brightness(img.pixels[current-img.width - 1])*hkernel[0][0] +
						p.brightness(img.pixels[current-img.width    ])*hkernel[0][1] +
						p.brightness(img.pixels[current-img.width + 1])*hkernel[0][2] +
						p.brightness(img.pixels[current          - 1])*hkernel[1][0] +
						p.brightness(img.pixels[current              ])*hkernel[1][1] +
						p.brightness(img.pixels[current           + 1])*hkernel[1][2] +
						p.brightness(img.pixels[current+img.width - 1])*hkernel[2][0] +
						p.brightness(img.pixels[current+img.width    ])*hkernel[2][1] +
						p.brightness(img.pixels[current+img.width + 1])*hkernel[2][2];*/

				//Optimisation: avoid the kernel array search
				sum_v = 
						p.brightness(img.pixels[current          - 1]) -		
						p.brightness(img.pixels[current           + 1]);

				sum_h = 	
						p.brightness(img.pixels[current-img.width    ]) -
						p.brightness(img.pixels[current+img.width    ]);

				sum = PApplet.sqrt(PApplet.pow(sum_h, 2) + PApplet.pow(sum_v, 2));

				//Optimisation: directly save the value in result (as the max value is always around 360, don't need to do a second loop)
				if (sum > (int)(360*0.3f)) {
					result.pixels[(y*img.width+x)] = p.color(255);
				} else {
					result.pixels[(y*img.width+x)] = p.color(0);
				}
			}
		}
		result.updatePixels();  
		return result;
	}

	public PImage pipelineToSobel(PImage img) {

		//Detect the board with color thresholding
		PImage boardHSV = colorThresholding(img); 

		//Apply gaussian filter to blur the image
		PImage boardBlur = convolute(boardHSV, gaussian, 99);

		//Apply Intensity thresholding
		//Optimisation: don't apply this thresholding to avoid too much computation as the resulat is already good enough
		//PImage boardIntensity = intensityThresholding(boardBlur, 200);

		//Apply sobel algorithm to detect the edges
		//PImage boardEdges = sobel(boardIntensity);
		PImage boardEdges = sobel(boardBlur);

		return boardEdges;
	}

}

package Synthetic_Noise;

import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Gaussian_Noise implements PlugInFilter {

	static double SIGMA = 5.0;

	public int setup(String args, ImagePlus imp) {
		return DOES_32;	// works only for FloatProcessor
	}

	public void run(ImageProcessor ip) {
		addGaussianNoise((FloatProcessor) ip, SIGMA);
	}

	void addGaussianNoise (FloatProcessor I, double sigma) {
		int w = I.getWidth();
		int h = I.getHeight();
		Random rnd = new Random();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				float val = I.getf(u, v);
				float noise = (float) (rnd.nextGaussian() * sigma);
				I.setf(u, v, val + noise);
			}
		}
	}

}

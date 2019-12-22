package Spectral_2;


import Spectral_2.dct.DCT;
import Spectral_2.dct.Util;
import ij.ImagePlus;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin calculates the forward DCT of the 
 * input grayscale image and displays the resulting spectrum 
 * (of type FloatProcessor) in a new image.
 */
public class DCT_Forward implements PlugInFilter {
	
	private ImagePlus im = null;

	public int setup(String args, ImagePlus im) {
		LogStream.redirectSystem();
		this.im = im;					// keep a reference to the original image
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		float[][] data = ip.getFloatArray();
		DCT.fDCT(data);		// apply forward DCT (data is modified!)
		Util.showAsImage(data, im.getShortTitle() + "-DCT", -500, 500);
	}
}

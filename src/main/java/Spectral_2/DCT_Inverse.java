package Spectral_2;

import Spectral_2.dct.DCT;
import Spectral_2.dct.Util;
import ij.ImagePlus;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin calculates the inverse DCT of the 
 * spectrum and displays the resulting spectrum 
 * (of type FloatProcessor) in a new image.
 */
public class DCT_Inverse implements PlugInFilter {
	
	private ImagePlus im = null;

	public int setup(String args, ImagePlus im) {
		LogStream.redirectSystem();
		this.im = im;
		return DOES_32 + NO_CHANGES;
	}

	public void run(ImageProcessor spectrum) {
		float[][] data = spectrum.getFloatArray();
		DCT.iDCT(data);		// apply forward DCT (data is modified!)
		Util.showAsImage(data, im.getShortTitle() + "-reconstructed", 0, 255);
	}
}

package Spectral_2;


import Spectral_2.dct.DCT;
import Spectral_2.dct.Util;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin calculates the forward DCT of the 
 * input grayscale image and displays the resulting spectrum 
 * (of type FloatProcessor) in a new image.
 * @deprecated
 */
public class DCT_Forward implements PlugInFilter {
	
	static boolean showLogSpectrum = true;
	
	private ImagePlus im = null;

	public int setup(String args, ImagePlus im) {
		LogStream.redirectSystem();
		this.im = im;					// keep a reference to the original image
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
//		if (!runDialog()) return;
		float[][] data = ip.getFloatArray();
		DCT.fDCT(data);		// apply forward DCT (data is modified!)
		//Util.showAsImage(data, im.getShortTitle() + "-DCT", -500, 500);
		//Util.showAsImage(data, im.getShortTitle() + "-DCT");
		FloatProcessor spectrum = new FloatProcessor(data);
//		if (showLogSpectrum) {
//			spectrum.abs();
//			spectrum.add(1);
//			spectrum.log();
//		}
		new ImagePlus(im.getShortTitle() + "-DCT", spectrum).show();
	}
	
//	private boolean runDialog() {
//		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
//		gd.addCheckbox("Show logarithmic spectrum", showLogSpectrum);
//		gd.showDialog(); 
//		if (gd.wasCanceled()) 
//			return false;
//		showLogSpectrum = gd.getNextBoolean();
//		return true;
//	}
}

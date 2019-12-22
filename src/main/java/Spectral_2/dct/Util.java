package Spectral_2.dct;

import ij.ImagePlus;
import ij.process.FloatProcessor;

public abstract class Util {

	public static ImagePlus makeImage(float[][] data, String title) {
		return makeImage(data, title, Double.NaN, Double.NaN);
	}
	
	public static ImagePlus makeImage(float[][] data, String title, double min, double max) {
		FloatProcessor fp = new FloatProcessor(data);
		ImagePlus imp = new ImagePlus(title, fp);
		if (Double.isFinite(min) && Double.isFinite(max)) {
			imp.setDisplayRange(min, max);
		}
		return imp;
	}
	
	/**
	 * Utility method for displaying float data. 
	 * Min/max data values are automatically mapped to black and white.
	 * @param data the pixel data
	 * @param title the image title
	 */
	public static ImagePlus showAsImage(float[][] data, String title) {
		return showAsImage(data, title, Double.NaN, Double.NaN);
	}
	
	/**
	 * Utility method for displaying float data with additional parameters
	 * for specifying the range of displayed values.
	 * @param data the pixel data
	 * @param title the image title
	 * @param min data value mapped to black
	 * @param max data value mapped to white
	 */
	public static ImagePlus showAsImage(float[][] data, String title, double min, double max) {
		ImagePlus imp = makeImage(data, title, min, max);
		if (imp != null)
			imp.show();
		return imp;
	}

}

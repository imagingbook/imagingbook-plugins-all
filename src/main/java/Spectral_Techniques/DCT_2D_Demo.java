/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Spectral_Techniques;


import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dct.Dct2d;

/** 
 * Calculates and displays the 2-dimensional DCT after converting the input image to a float image.
 * of arbitrary size. Be patient, this is not optimized and thus slow!
 * @author W. Burger
 * @version 2014-04-13
 */
public class DCT_2D_Demo implements PlugInFilter {
	
	static boolean showLogarithmicSpectrum = true;
	static boolean reconstructInputImage = false;

	public int setup(String arg, ImagePlus im) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog()) return;

		FloatProcessor fp = ip.convertToFloatProcessor();

		// create a new DCT instance:
		Dct2d dct = new Dct2d();

		// calculate the forward DCT:
		FloatProcessor spectrum = dct.DCT(fp);

		// modify the spectrum for viewing and show it:
		if (showLogarithmicSpectrum) {
			spectrum.abs();
			spectrum.add(1.0);
			spectrum.log();
		}
		spectrum.resetMinAndMax();
		new ImagePlus("DCT Spectrum", spectrum).show();

		// reconstruct the image by the inverse DCT:
		if (reconstructInputImage) {
			FloatProcessor reconstruction = dct.iDCT(spectrum);
			new ImagePlus("DCT Reconstruction", reconstruction).show();
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Show logarithmic spectrum", showLogarithmicSpectrum);
		gd.addCheckbox("Reconstruct the input image", reconstructInputImage);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		showLogarithmicSpectrum = gd.getNextBoolean();
		reconstructInputImage = gd.getNextBoolean();
		return true;
	}


}

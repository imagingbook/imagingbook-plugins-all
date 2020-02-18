/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
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
 * of arbitrary size.
 * Optionally, either a direct DCT or a fast implementation is used. 
 * @author W. Burger
 * @version 2019-12-26
 */
public class DCT_2D_Demo implements PlugInFilter {
	
	static boolean useFastMode = true;
	static boolean showLogSpectrum = true;
	static boolean reconstructImage = false;

	public int setup(String arg, ImagePlus im) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog()) return;

		FloatProcessor fp = ip.convertToFloatProcessor();
		float[][] g = fp.getFloatArray();

		// create a new DCT instance:
		Dct2d.Float dct = new Dct2d.Float();
		dct.useFastMode(useFastMode);
		
		// calculate the forward DCT:
		dct.forward(g);

		FloatProcessor spectrum = new FloatProcessor(g);
		if (showLogSpectrum) {
			spectrum.abs();
			spectrum.add(1.0);
			spectrum.log();
		}
		spectrum.resetMinAndMax();
		new ImagePlus("DCT Spectrum", spectrum).show();
		
		// ----------------------------------------------------
		
		if (reconstructImage) {
			dct.inverse(g);
			FloatProcessor reconstructed = new FloatProcessor(g);
			reconstructed.setMinAndMax(0, 255);
			new ImagePlus("Reconstructed image", reconstructed).show();
		}

	}
	
	// ---------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use fast transform", useFastMode);
		gd.addCheckbox("Show absolute/log spectrum", showLogSpectrum);
		gd.addCheckbox("Reconstruct the input image", reconstructImage);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		useFastMode = gd.getNextBoolean();
		showLogSpectrum = gd.getNextBoolean();
		reconstructImage = gd.getNextBoolean();
		return true;
	}


}

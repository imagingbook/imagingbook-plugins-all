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
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.dft.Dft2d;

/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 * TODO: adapt API to the DCT layout.
 * @deprecated
 */
public class DFT_2D_Demo implements PlugInFilter{

	static boolean showCenteredSpectrum = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog()) return;
		FloatProcessor fp = ip.convertToFloatProcessor();
		Dft2d dft = new Dft2d(fp, showCenteredSpectrum);

		ImageProcessor ipP = dft.makePowerImage();
		ImagePlus win = new ImagePlus("DFT Power Spectrum (byte)", ipP);
		win.show();
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Show centered spectrum", showCenteredSpectrum);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		showCenteredSpectrum = gd.getNextBoolean();
		return true;
	}

}

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
import imagingbook.pub.dft.Dft2d;


/** 
 * This ImageJ plugin computes the 2-dimensional (power-spectrum) DFT on an image
 * of arbitrary size using float arrays.
 * Optionally, either a direct DFT or a fast FFT implementation is used. 
 */
public class DFT_2D_Float_Demo implements PlugInFilter {
	
	static boolean showLogSpectrum = true;
	static boolean useFastMode = true;
	static boolean reconstructImage = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog()) 
			return;
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		float[][] re = fp.getFloatArray();
		float[][] im = new float[fp.getWidth()][fp.getHeight()];
		
		Dft2d.Float dft2 = new Dft2d.Float();
		dft2.useFastMode(useFastMode);
		
		dft2.forward(re, im);
		
		float[][] mag = dft2.getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		if (showLogSpectrum) {
			ms.add(1.0);
			ms.log();
		}
		ms.resetMinAndMax();
		new ImagePlus("DFT Magnitude Spectrum", ms).show();
		
		// ----------------------------------------------------
		
		if (reconstructImage) {
			dft2.inverse(re, im);
			FloatProcessor reIp = new FloatProcessor(re);
			reIp.setMinAndMax(0, 255);
			new ImagePlus("Reconstructed image (real part)", reIp).show();
			
			FloatProcessor imIp = new FloatProcessor(im);
			imIp.setMinAndMax(0, 255);
			new ImagePlus("Reconstructed image (imaginary part)",imIp).show();
		}
	}
	
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use FFT", useFastMode);
		gd.addCheckbox("Show logarithmic spectrum", showLogSpectrum);
		gd.addCheckbox("Reconstruct image", reconstructImage);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		useFastMode = gd.getNextBoolean();
		showLogSpectrum = gd.getNextBoolean();
		reconstructImage = gd.getNextBoolean();
		return true;
	}

}

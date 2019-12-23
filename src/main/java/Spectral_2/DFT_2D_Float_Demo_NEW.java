/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Spectral_2;

import Spectral_2.dft.Dft2D;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 * TODO: adapt API to the DCT layout.
 */
public class DFT_2D_Float_Demo_NEW implements PlugInFilter {
	
	boolean LOGARITHMIC = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		float[][] re = fp.getFloatArray();
		float[][] im = new float[fp.getWidth()][fp.getHeight()];
		
		Dft2D.Float dft2 = new Dft2D.Float();
		dft2.forward(re, im);
		
		float[][] mag = getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		
		if (LOGARITHMIC) {
			ms.add(1.0);
			ms.log();
		}
		
		ms.resetMinAndMax();
		new ImagePlus("DFT Magnitude Spectrum", ms).show();
//		
//		// ----------------------------------------------------
		
		dft2.inverse(re, im);
		FloatProcessor reIp = new FloatProcessor(re);
		reIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Re", reIp).show();
		
		FloatProcessor imIp = new FloatProcessor(im);
		imIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Im",imIp).show();
	}
	
	private float[][] getMagnitude(float[][] re, float[][] im) {
		final int width = re.length;
		final int height = re[0].length;
		float[][] mag = new float[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float a = re[u][v];
				float b = im[u][v];
				mag[u][v] = (float) Math.sqrt(a * a + b * b);
			}
		}
		return mag;
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Spectral_2;

import Spectral_2.dft.Dft2DImpl;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;


/** 
 * Computes the 2-dimensional (power-spectrum) DFT on an image
 * of arbitrary size using double arrays. 
 * Note that this is rather wasteful in terms of resources and
 * only for demonstration purposes.
 *
 */
public class DFT_2D_Double_Demo_NEW implements PlugInFilter {
	
	boolean LOGARITHMIC = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		double[][] re = Matrix.toDouble(fp.getFloatArray());
		double[][] im = new double[fp.getWidth()][fp.getHeight()];
		
		Dft2DImpl.Double dft2 = new Dft2DImpl.Double();
		dft2.forward(re, im);
		
		double[][] mag = getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(Matrix.toFloat(mag));
		
		if (LOGARITHMIC) {
			ms.add(1.0);
			ms.log();
		}
		
		ms.resetMinAndMax();
		new ImagePlus("DFT Magnitude Spectrum", ms).show();
//		
//		// ----------------------------------------------------
		
		dft2.inverse(re, im);
		FloatProcessor reIp = new FloatProcessor(Matrix.toFloat(re));
		reIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Re", reIp).show();
		
		FloatProcessor imIp = new FloatProcessor(Matrix.toFloat(im));
		imIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Im",imIp).show();
	}
	
	private double[][] getMagnitude(double[][] re, double[][] im) {
		final int width = re.length;
		final int height = re[0].length;
		double[][] mag = new double[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				double a = re[u][v];
				double b = im[u][v];
				mag[u][v] = Math.sqrt(a * a + b * b);
			}
		}
		return mag;
	}

}

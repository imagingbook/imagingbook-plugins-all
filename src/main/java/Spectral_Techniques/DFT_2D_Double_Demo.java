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
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.dft.DFT2;

/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 * TODO: adapt API to the DCT layout.
 */
public class DFT_2D_Double_Demo implements PlugInFilter {
	
	boolean LOGARITHMIC = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		double[][] re = IjUtils.toDoubleArray(fp);
		double[][] im = new double[fp.getWidth()][fp.getHeight()];
		
		DFT2.Double dft2 = new DFT2.Double();
		dft2.applyTo(re, im, true);
		
//		FloatProcessor ms = IjUtils.toFloatProcessor(DFT2.Double.getMagnitude(re, im));
//		FloatProcessor ms = new FloatProcessor(Matrix.toFloat(DFT2.Double.getMagnitude(re, im)));
		FloatProcessor ms = new FloatProcessor(DFT2.Float.getMagnitude(
									Matrix.toFloat(re), Matrix.toFloat(im)));
		
		if (LOGARITHMIC) {
			ms.add(1.0);
			ms.log();
		}
		
		ms.resetMinAndMax();
		new ImagePlus("DFT Magnitude Spectrum", ms).show();
		
		// ----------------------------------------------------
		
		dft2.applyTo(re, im, false);
		FloatProcessor reIp = IjUtils.toFloatProcessor(re);
		reIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Re", reIp).show();
		
		FloatProcessor imIp = IjUtils.toFloatProcessor(im);
		imIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Im", imIp).show();
	}
	
//	private FloatProcessor toFloatProcessor(double[][] da) {
//		final int width = da.length;
//		final int height = da[0].length;
//		float[][] fa = new float[width][height];
//		for (int u = 0; u < width; u++) {
//			for (int v = 0; v < height; v++) {
//				fa[u][v] = (float) da[u][v];
//			}
//		}
//		return new FloatProcessor(fa);
//	}
}

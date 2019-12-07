/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Spectral_Techniques;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dft.Dft2d;

/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 * TODO: adapt API to the DCT layout.
 */
public class DFT_2D_Demo implements PlugInFilter{

	static boolean center = true;    //center the resulting spectrum?
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		IJ.log("runnung DFT_2D_Demo");
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		//Dft2d dft = new Dft2d(fp, center);
		
		double[][][] g = toComplexArray(fp);
		Dft2d dft2D = new Dft2d(ip.getWidth(), ip.getHeight());
		double[][][] G = dft2D.dft(g);

		ImageProcessor ipP = dft2D.makePowerImage();
		ImagePlus win = new ImagePlus("DFT Power Spectrum (byte)", ipP);
		win.show();
	}
	
	private double[][][] toComplexArray(FloatProcessor fp) {
		final int width = fp.getWidth();
		final int height = fp.getHeight();
		float[][] pixels = fp.getFloatArray(); 
		double[][][] arr = new double[width][height][2];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				arr[u][v][0] = pixels[u][v];
				arr[u][v][1] = 0;
			}
		}
		return arr;
	}

}

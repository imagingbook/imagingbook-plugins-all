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
		
		double[][] re = toDoubleArray(fp);
		double[][] im = toDoubleArray(fp, false);
		
		Dft2d dft2 = new Dft2d();
		dft2.dft(re, im, true);
		
		float[][] mag = getLogMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		
		ms.resetMinAndMax();
		new ImagePlus("DFT Magnitude Spectrum", ms).show();
		
		// ----------------------------------------------------
		
		dft2.dft(re, im, false);
		FloatProcessor reIp = toFloatProcessor(re);
		reIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Re",reIp).show();
		
		FloatProcessor imIp = toFloatProcessor(im);
		imIp.setMinAndMax(0, 255);
		new ImagePlus("Reconstructed Im",imIp).show();
		
		
	}
	
	private FloatProcessor toFloatProcessor(double[][] da) {
		final int width = da.length;
		final int height = da[0].length;
		float[][] fa = new float[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				fa[u][v] = (float) da[u][v];
			}
		}
		return new FloatProcessor(fa);
	}
	
	private double[][] toDoubleArray(FloatProcessor fp) {
		return toDoubleArray(fp, true);
	}
	
	private double[][] toDoubleArray(FloatProcessor fp, boolean copyValues) {
		final int width = fp.getWidth();
		final int height = fp.getHeight();
		double[][] arr = new double[width][height];
		if (copyValues) {
			float[][] pixels = fp.getFloatArray(); 
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					arr[u][v] = pixels[u][v];
				}
			}
		}
		return arr;
	}
	
	private double[][] nullDoubleArray(ImageProcessor fp) {
		final int width = fp.getWidth();
		final int height = fp.getHeight();
		return new double[width][height];
	}
	
	private float[][] getLogMagnitude(double[][] re, double[][] im) {
		final int width = re.length;
		final int height = re[0].length;
		float[][] ps = new float[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				double a = re[u][v];
				double b = im[u][v];
				ps[u][v] = (float) Math.log(1 + Math.sqrt(a * a + b * b));
			}
		}
		return ps;
	}

}

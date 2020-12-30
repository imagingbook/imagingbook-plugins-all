/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _Demos;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GaussianFilterSeparable;
import imagingbook.lib.filters.Kernel2D;
import imagingbook.lib.filters.LinearFilter2D;
import imagingbook.lib.filters.LinearFilter2DSeparable;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.Matrix;

/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter2D} and {@link Kernel2D}.
 * This plugin works for all types of images.
 * 
 * @author WB
 *
 */
public class Gaussian_Filter_Separable implements PlugInFilter {
	
	static double SIGMA = 3.0;

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	LinearFilter2DSeparable filter = new GaussianFilterSeparable(SIGMA);
		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
		filter.applyTo(ip);
		
//		float[] kernelX = filter.getKernelX();
//		float[] kernelY = filter.getKernelY();
//		
//		float[][] kernel = new float[kernelX.length][kernelY.length];
//		for (int x = 0; x < kernelX.length; x++) {
//			for (int y = 0; y < kernelY.length; y++) {
//				kernel[y][x] = kernelX[x] * kernelY[y];
//			}
//		}
		
		float[][] kernel = filter.getKernel().getH();
//		
		IJ.log(Matrix.toString(kernel));
		
		(IjUtils.createImage("", kernel)).show();
		IJ.log("sum = " + Matrix.sum(kernel));
		
		FloatProcessor impulse = new FloatProcessor(100, 100);
		impulse.setf(50, 50, 1.0f);
		filter.applyTo(impulse);
		(new ImagePlus("Impulse", impulse)).show();
    }

}

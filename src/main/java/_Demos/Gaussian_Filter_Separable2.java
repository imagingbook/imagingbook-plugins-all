/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _Demos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.linear.GaussianKernel1D;
import imagingbook.lib.filter.linear.Kernel1D;
import imagingbook.lib.filter.linear.Kernel2D;
import imagingbook.lib.filter.linear.LinearFilter;
import imagingbook.lib.filter.linear.LinearFilterSeparable;
import imagingbook.lib.image.access.PixelPack;


/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter} and {@link Kernel2D}.
 * This plugin works for all types of images.
 * 
 * @author WB
 *
 */
public class Gaussian_Filter_Separable2 implements PlugInFilter {
	
	static double SIGMA = 3.0;

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {	
		Kernel1D kernel = new GaussianKernel1D(SIGMA);
		new LinearFilterSeparable(kernel).applyTo(PixelPack.pack(ip));
    }

}

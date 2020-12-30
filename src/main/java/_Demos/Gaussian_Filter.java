/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _Demos;

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import imagingbook.lib.filters.GaussianFilter;
import imagingbook.lib.filters.Kernel2D;
import imagingbook.lib.filters.LinearFilter2D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter2D} and {@link Kernel2D}.
 * This plugin works for all types of images.
 * 
 * @author WB
 *
 */
public class Gaussian_Filter implements PlugInFilter {
	
	static double SIGMA = 3.0;

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
		GaussianFilter filter = new GaussianFilter(SIGMA);
		filter.setOutOfBoundsStrategy(OutOfBoundsStrategy.NEAREST_BORDER);	// default
		filter.applyTo(ip);
    }

}

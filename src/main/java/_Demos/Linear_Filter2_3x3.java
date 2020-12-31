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
import imagingbook.lib.filter.LinearFilter;
import imagingbook.lib.filter.kernel.Kernel2D;
import imagingbook.lib.filtersOBSOLETE.LinearFilter2D;

/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter2D} and {@link Kernel2D}.
 * This plugin works for all types of images.
 * 
 * @author WB
 *
 */
public class Linear_Filter2_3x3 implements PlugInFilter {
	
//	private static float[][] H = {
//			{1, 2, 1},
//			{2, 4, 2},
//			{1, 2, 1}};
//	
	private static float[][] H = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}};

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
		LinearFilter filter = new LinearFilter(ip, new Kernel2D(H), null);
		(new ImagePlus("Result", filter.apply())).show();
    }

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Pixel_Interpolation;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.image.OutOfBoundsStrategy;
import static imagingbook.lib.image.OutOfBoundsStrategy.*;
import imagingbook.lib.interpolation.InterpolationMethod;
import static imagingbook.lib.interpolation.InterpolationMethod.*;


public class Interpolator_Demo_Old implements PlugInFilter {
	
	static double dx = 0.5;	// translation
	static double dy = -3.5;
	
	static OutOfBoundsStrategy OBS = NearestBorder;
	static InterpolationMethod IPM = BicubicSharp;
 
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL + NO_CHANGES;
    }

    public void run(ImageProcessor source) {
    	final int w = source.getWidth();
    	final int h = source.getHeight();
    	
    	// create the target image (same type as source):
    	ImageProcessor target = source.createProcessor(w, h);
    	
    	// create an ImageAccessor for the source image:
    	ImageAccessor sA = ImageAccessor.create(source, OBS, IPM);
    	
    	// create an ImageAccessor for the target image:
    	ImageAccessor tA = ImageAccessor.create(target);
    	
    	// iterate over all pixels of the target image:
    	for (int u = 0; u < w; u++) {	// discrete target position (u,v)
    		for (int v = 0; v < h; v++) {
    			double x = u + dx;	// continuous source position (x,y)
    			double y = v + dy;
    			float[] val = sA.getPix(x, y);
    			tA.setPix(u, v, val);	// update target pixel
    		}
    	}
    	
    	// display the target image:
    	(new ImagePlus("Target", target)).show();
    }
}

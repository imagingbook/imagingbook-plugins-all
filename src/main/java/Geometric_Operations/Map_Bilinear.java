/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageMapper;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.basic.Pnt2d.PntInt;
import imagingbook.pub.geometry.mappings.nonlinear.BilinearMapping2D;

public class Map_Bilinear implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
	
	   	Pnt2d[] P = {
				PntInt.from(0, 0),
				PntInt.from(400, 0),
				PntInt.from(400, 400),
				PntInt.from(0, 400)
	    	};

	    	Pnt2d[] Q = {
				PntInt.from(0, 60),
				PntInt.from(400, 20),
				 PntInt.from(300, 400),
				 PntInt.from(30, 200)
	    	};
		
		// we want the inverse mapping (Q -> P, so we swap P/Q):
		BilinearMapping2D mi = BilinearMapping2D.fromPoints(Q, P);
		new ImageMapper(mi, null, InterpolationMethod.Bicubic).map(ip);
    }
}

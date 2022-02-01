/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ColorMode;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilterScalar;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilterVector;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * 
 * @author W. Burger
 * @version 2022/02/01
 */
public class Perona_Malik_Demo implements PlugInFilter {

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		// create a parameter object, modify settings if needed:
		Parameters params = new Parameters();
		params.iterations = 20;
		params.alpha = 0.15f;
		params.kappa = 20.0f;

		// create the actual filter:
		GenericFilter filter = null;
		if (ip instanceof ColorProcessor) {
			params.colorMode = ColorMode.ColorGradient;
			filter = new PeronaMalikFilterVector(params);
		}
		else {
			filter = new PeronaMalikFilterScalar(params);
		}
		
		// apply the filter:
		filter.applyTo(ip);
	}
	
}




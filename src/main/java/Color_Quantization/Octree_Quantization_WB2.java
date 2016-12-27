/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;


import Color_Quantization.lib.wilbur2.OctreeQuantizer;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;



/**
 * Works, but results are REALLY disappointing!!
 * Needs to be checked against other implementations.
 * 
 * @author WB
 *
 */
public class Octree_Quantization_WB2 implements PlugInFilter {
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		IJ.log("creating octree quantizer");
		
		OctreeQuantizer oq = new OctreeQuantizer(cp, NCOLORS);
//		OctreeQuantizer oq = new OctreeQuantizer((int[]) cp.getPixels(), NCOLORS);
		IJ.log("quantizer is done");
		
		ColorProcessor cp2 = (ColorProcessor) cp.duplicate();
		int[] pixels2 = (int[]) cp2.getPixels();
		
		for (int i = 0; i < pixels2.length; i++) {
			pixels2[i] = oq.mapColor(pixels2[i]);
		}
		
		(new ImagePlus("Quantized", cp2)).show();
	}


}

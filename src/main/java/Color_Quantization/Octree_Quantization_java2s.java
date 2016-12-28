/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;



import java.awt.Image;

import Color_Quantization.lib.java2s.Quantize;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * // http://www.java2s.com/Code/Java/2D-Graphics-GUI/Anefficientcolorquantizationalgorithm.ht
 * 
 * UNFINISHED!
 *
 */

public class Octree_Quantization_java2s implements PlugInFilter {
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		IJ.log("sun quantizer (java2s)");
		
		ColorProcessor cp = ip.convertToColorProcessor();
		int[][] pixels = cp.getIntArray();
		IJ.log("pixels.length = " + pixels.length);
		int[] lut = Quantize.quantizeImage(pixels, NCOLORS);
		
		for (int i=0; i< lut.length; i++) {
			IJ.log(String.format("   lut i=%d: %d", i, lut[i]));
		}
		
		ColorProcessor cp2 = (ColorProcessor) cp.duplicate();
		for (int u = 0; u < pixels.length; u++) {
			for (int v = 0; v < pixels[u].length; v++) {
				cp2.putPixel(u, v, pixels[u][v]);
			}
		}
		
		ImagePlus im2 = new ImagePlus("quantized", cp2);
		im2.show();
	}


}

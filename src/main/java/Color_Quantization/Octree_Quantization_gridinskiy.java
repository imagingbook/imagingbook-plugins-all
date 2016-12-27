/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;



import Color_Quantization.lib.gridinskiy.QuantizerGridinskiy;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjLogStream;

/**
 * DOES NOT SEM TO WORK - makes garbage

 * @author WB
 *
 */

public class Octree_Quantization_gridinskiy implements PlugInFilter {
	
	static {
		IjLogStream.redirectSystem();
	}
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		IJ.log("sun quantizer (PaletteBuilder)");
		
		QuantizerGridinskiy q = new QuantizerGridinskiy(ip.getBufferedImage(), NCOLORS);
		
		
		ImagePlus im2 = new ImagePlus("quantized", q.out);
		im2.show();
	}


}

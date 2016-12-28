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
import java.awt.image.RenderedImage;

import Color_Quantization.lib.sun.PaletteBuilder;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * https://github.com/frohoff/jdk8u-jdk/blob/master/src/share/classes/com/sun/imageio/plugins/common/PaletteBuilder.java
 * Uses sun/imageio/plugins/common/PaletteBuilder (openjdk-8)
 * 
 * WORKS GREAT, but not for small number of colors!
 * @author WB
 *
 */

public class Octree_Quantization_sun implements PlugInFilter {
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		IJ.log("sun quantizer (PaletteBuilder)");
		
		RenderedImage newimg = PaletteBuilder.createIndexedImage(ip.getBufferedImage(), NCOLORS);
		
		ImagePlus im2 = new ImagePlus("quantized", (Image) newimg);
		im2.show();
	}


}

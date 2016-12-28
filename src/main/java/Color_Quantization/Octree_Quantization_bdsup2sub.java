/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;




import Color_Quantization.lib.bdsup2sub.OctTreeQuantizer;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;



/**
 * http://www.javased.com/index.php?source_dir=BDSup2Sub/src/main/java/bdsup2sub/tools/QuantizeFilter.java
 * 
 * Works somehow. Results look strange.
 * Problems with small color counts.
 * 
 * 
 * @author WB
 *
 */
public class Octree_Quantization_bdsup2sub implements PlugInFilter {
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		int[] pixels = (int[]) cp.getPixels();
		IJ.log("creating octree quantizer (bdsup2sub)");
		
		OctTreeQuantizer oq = new OctTreeQuantizer();
//		oq.setup(NCOLORS);
//		oq.addPixels(pixels, 0, pixels.length);
//
//		int[] colorTable = oq.buildColorTable();
		
		int[] colorTable = new int[NCOLORS];
		
		oq.buildColorTable(pixels, colorTable);

		IJ.log("quantizer is done, table length = " + colorTable.length);
		
		
		ColorProcessor cp2 = (ColorProcessor) cp.duplicate();
		int[] pixels2 = (int[]) cp2.getPixels();
		
		for (int i = 0; i < pixels2.length; i++) {
			pixels2[i] = colorTable[oq.getIndexForColor(pixels[i])];
		}
		
		(new ImagePlus("Quantized", cp2)).show();
	}


}

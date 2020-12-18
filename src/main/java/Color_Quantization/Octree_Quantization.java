/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package Color_Quantization;


import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.quantize.OctreeQuantizer;
import imagingbook.pub.color.quantize.OctreeQuantizer.Parameters;

/**
 * ImageJ plugin demonstrating the use of the {@link OctreeQuantizer} class.
 * 
 * @author WB
 * @version 2017/01/03
 */
public class Octree_Quantization implements PlugInFilter {
	
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_TABLE = false;

	static {
		LogStream.redirectSystem();
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		Parameters params = new Parameters();
		
		if (!showDialog(params))
			return;

		ColorProcessor cp = (ColorProcessor) ip;
		int[] pixels = (int[]) cp.getPixels();

		OctreeQuantizer quantizer = new OctreeQuantizer(pixels, params);
		int nCols = quantizer.getColorMap().length;
		
		String qck = params.quickQuantization ? " quick" : "";
		
		if (CREATE_INDEXED_IMAGE) {
			// quantize to an indexed color image
			ByteProcessor idxIp = quantizer.quantize(cp);
			(new ImagePlus("Quantized Index Color Image (" + nCols + " colors)" + qck, idxIp)).show();
		}
		
		if (CREATE_RGB_IMAGE) {
			// quantize to a full-color RGB image
			int[] rgbPix = quantizer.quantize((int[]) pixels);
			ColorProcessor rgbIp = new ColorProcessor(cp.getWidth(), cp.getHeight(), rgbPix);
			(new ImagePlus("Quantized RGB Image (" + nCols + " colors)" + qck, rgbIp)).show();
		}
		
		if (LIST_COLOR_TABLE) {
			quantizer.listColorMap();
		}
	}
	
	private boolean showDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(Median_Cut_Quantization.class.getSimpleName());
		gd.addNumericField("No. of colors (2,..,256)", params.maxColors, 0);
		gd.addCheckbox("Use quick quantization", params.quickQuantization);
		gd.addCheckbox("Create indexed color image", CREATE_INDEXED_IMAGE);
		gd.addCheckbox("Create quantized RGB image", CREATE_RGB_IMAGE);
		gd.addCheckbox("List quantized color table", LIST_COLOR_TABLE);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		int nc = (int) gd.getNextNumber();
		nc = Math.min(nc, 255);
		nc = Math.max(2, nc);
		
		params.maxColors = nc;
		params.quickQuantization = gd.getNextBoolean();
		
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_TABLE = gd.getNextBoolean();
		return true;
	}

//	private void shuffleArray(int[] ar) {
//		Random rnd = ThreadLocalRandom.current();
//		for (int i = ar.length - 1; i > 0; i--) {
//			int index = rnd.nextInt(i + 1);
//			// Simple swap
//			int a = ar[index];
//			ar[index] = ar[i];
//			ar[i] = a;
//		}
//	}

}

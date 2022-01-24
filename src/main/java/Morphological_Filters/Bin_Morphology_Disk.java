/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Morphological_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.GuiTools;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.morphology.BinaryClosing;
import imagingbook.pub.morphology.BinaryDilation;
import imagingbook.pub.morphology.BinaryErosion;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.morphology.BinaryMorphologyOperator;
import imagingbook.pub.morphology.BinaryOpening;


/**
 * This plugin implements a binary morphology filter using a disk-shaped
 * structuring element whose radius can be specified.
 */
public class Bin_Morphology_Disk implements PlugInFilter {
	
	private enum OpType {
		Dilate, Erode, Open, Close;
	}

	private static OpType op = OpType.Dilate;
	private static double radius = 1.0;
	private static boolean showStructuringElement = false;

	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		
		if (!showDialog()) {
			return;
		}
		
		ByteProcessor bp = (ByteProcessor) ip;
		byte[][] H = BinaryMorphologyFilter.makeDiskKernel(radius);
		BinaryMorphologyOperator bmf = null; // new BinaryMorphologyFilter.Disk(radius);

		switch(op) {
		case Close:
			bmf = new BinaryClosing(H); break;
		case Dilate:
			bmf = new BinaryDilation(H); break;
		case Erode:
			bmf = new BinaryErosion(H); break;
		case Open:
			bmf = new BinaryOpening(H); break;
		}
		
		bmf.applyTo(bp);
		
		if (showStructuringElement) {
			ByteProcessor pH = IjUtils.toByteProcessor(H);
			pH.invertLut();
			pH.setMinAndMax(0, 1);
			ImagePlus iH = new ImagePlus("H", pH);
			iH.show();
			GuiTools.zoomExact(iH, 10);
		}
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Structuring Element (Disk)");
		gd.addNumericField("Radius (filters only)", 1.0, 1, 5, "pixels");
		gd.addEnumChoice("Operation", OpType.Dilate);
		gd.addCheckbox("Show structuring element", showStructuringElement);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		radius = gd.getNextNumber();
		op = gd.getNextEnumChoice(OpType.class);
		showStructuringElement = gd.getNextBoolean();
		return true;
	}
	
}

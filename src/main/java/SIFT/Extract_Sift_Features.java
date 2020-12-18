/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package SIFT;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.sift.SiftDescriptor;
import imagingbook.pub.sift.SiftDetector;
import imagingbook.pub.sift.util.Colors;
import imagingbook.pub.sift.util.SiftOverlay;

/**
 * This plugin extracts multi-scale SIFT features from the current 
 * image and displays them as M-shaped markers.
 * List of keypoints (if selected) is sorted by descending magnitude.
 * <br>
 * 2020/10/04: changed to use {@link SiftOverlay} (subclass of {@link imagingbook.lib.ij.CustomOverlay})
 *  
 * @author W. Burger
 * @version 2020/10/04
 */

public class Extract_Sift_Features implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
	}

	private static double FeatureScale = 1.0; // 1.5;
	private static double FeatureStrokewidth = 0.5;
	private static boolean ListSiftFeatures = false;

	ImagePlus imp;
	Color[] colors = Colors.DefaultDisplayColors;
	SiftDetector.Parameters params;

	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		params =  new SiftDetector.Parameters();
						
		if (!showDialog()) {
			return;
		}
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		SiftDetector sd = new SiftDetector(fp, params);
		List<SiftDescriptor> features = sd.getSiftFeatures();
		
		if (ListSiftFeatures) {
			int i = 0;
			for (SiftDescriptor sf : features) {
				IJ.log(i + ": " + sf.toString());
				i++;
			}
		}

		ImageProcessor ip2 = ip.duplicate();
		ImagePlus imp2 = new ImagePlus(imp.getShortTitle() + "-SIFT", ip2);
	
		SiftOverlay oly = new SiftOverlay();
		oly.setFeatureScale(FeatureScale);
		oly.strokeWidth(FeatureStrokewidth);
		for (SiftDescriptor sf : features) {
			oly.strokeColor(colors[sf.getScaleLevel() % colors.length]);
			oly.addItem(sf);
		}

		imp2.setOverlay(oly);
		imp2.show();
	}
	
	private boolean showDialog() {
			GenericDialog gd = new GenericDialog("Set SIFT parameters");
			gd.addNumericField("tMag :", params.t_Mag, 3, 6, "");
			gd.addNumericField("rMax :", params.rho_Max, 3, 6, "");
			gd.addNumericField("orientation histogram smoothing :", params.n_Smooth, 0, 6, "");
			gd.addCheckbox("list all SIFT features (might be many!)", ListSiftFeatures);
			gd.showDialog();
			if (gd.wasCanceled()) {
				return false;
			}
			params.t_Mag = gd.getNextNumber();
			params.rho_Max = gd.getNextNumber();
			params.n_Smooth = (int) gd.getNextNumber();
			ListSiftFeatures = gd.getNextBoolean();
			if(gd.invalidNumber()) {
				IJ.error("Input Error", "Invalid input number");
				return false;
			}	
			return true;
	}
	
}

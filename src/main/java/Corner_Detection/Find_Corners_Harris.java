/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Corner_Detection;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.corners.AbstractGradientCornerDetector;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;
import imagingbook.pub.corners.subpixel.MaxLocator.Method;
import imagingbook.pub.corners.utils.CornerOverlay;


/**
 * This plugin demonstrates the use of the Harris corner detector
 * (see {@link HarrisCornerDetector}).
 * It calculates the corner positions and shows them as a vector overlay
 * on top of the source image.
 * 
 * @author WB
 * @version 2020/10/03
 */
public class Find_Corners_Harris implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
	}
	
	static int nmax = 0;						// number of corners to show (0 = all)
	static double CornerSize = 2;				// size of cross-markers
	static Color CornerColor = Color.green;		// color of cross markers
	static double CornerStrokeWidth = 0.2;
	
	ImagePlus im;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL;
    }
    
    public void run(ImageProcessor ip) {
    	
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}
		
		AbstractGradientCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		CornerOverlay oly = new CornerOverlay();
		oly.setMarkerSize(CornerSize);
		oly.strokeColor(CornerColor);
		oly.strokeWidth(CornerStrokeWidth);
		oly.add(corners);

		im.setOverlay(oly);
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if cancelled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Smoothing radius (\u03C3)", params.sigma, 3);
		dlg.addNumericField("Sensitivity (\u03B1)", params.alpha, 3);
		dlg.addNumericField("Corner response threshold (th)", params.tH, 0);
		dlg.addChoice("Subpixel localization", 
				Enums.getEnumNames(Method.class), params.maxLocatorMethod.name()); // SubpixelMethod.None.name()
		// -----------
		dlg.addNumericField("Border distance", params.border, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Minimum corner distance", params.dmin, 0);
		
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.addNumericField("Corner display size", CornerSize, 1);
		
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		
		params.sigma = Math.max(0.5, dlg.getNextNumber());
		params.alpha = Math.max(0, dlg.getNextNumber());
		params.tH = dlg.getNextNumber();
		params.maxLocatorMethod = Method.valueOf(dlg.getNextChoice());
		// -----------
		params.border = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		params.dmin = (int) dlg.getNextNumber();
		
		nmax = (int) dlg.getNextNumber();
		CornerSize = dlg.getNextNumber();
		
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}
		
		return true;
	}
	
	//-------------------------------------------------------------------
	
	@SuppressWarnings("unused")
	private void listCorners(List<Corner> corners) {
		IJ.log(this.getClass().getSimpleName() + " - corners found: " + corners.size());
		for (Corner c : corners) {
			IJ.log(c.toString());
		}
	}
	
	// Brightens the image ip. May not work with ShortProcessor and FloatProcessor
	@SuppressWarnings("unused")
	private void brighten(ImageProcessor ip) {	
		int[] lookupTable = new int[256];
		for (int i = 0; i < 256; i++) {
			lookupTable[i] = 128 + (i / 2);
		}
		ip.applyTable(lookupTable); 
	}

}

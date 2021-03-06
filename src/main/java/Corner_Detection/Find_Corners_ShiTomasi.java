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
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.GenericDialogPlus;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.GradientCornerDetector;
import imagingbook.pub.corners.ShiTomasiDetector;
import imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator.Method;
import imagingbook.pub.corners.util.CornerOverlay;

/**
 * This plugin demonstrates the use of the Shi-Tomasi corner detector
 * (see {@link ShiTomasiDetector}).
 * It calculates the corner positions and shows them as a vector overlay
 * on top of the source image.
 * 
 * @see Find_Corners_Harris
 * @see Find_Corners_MOPS
 * 
 * @author WB
 * @version 2020/10/04
 */
public class Find_Corners_ShiTomasi implements PlugInFilter {
	
	private static int Nmax = 0;	// number of corners to show (0 = all)
	private static Color cornerColor = Color.green;
	
	private ImagePlus im;
	private ShiTomasiDetector.Parameters params;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL;
    }
    
    public void run(ImageProcessor ip) {
    	
    	params = new ShiTomasiDetector.Parameters();
		if (!showDialog()) {
			return;
		}
		
		GradientCornerDetector cd = new ShiTomasiDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		// create a vector overlay to mark the resulting corners
		CornerOverlay oly = new CornerOverlay();
		oly.strokeColor(cornerColor);
		oly.strokeWidth(0.25);
		oly.addItems(corners);
		im.setOverlay(oly);
		
		// (new ImagePlus("Shi-Tomasi Corner Score", cd.getQ())).show();
    }
    
	private boolean showDialog() {
		// display dialog , return false if cancelled or on error.
		GenericDialogPlus dlg = new GenericDialogPlus("Harris Corner Detector");
		
		dlg.addCheckbox("Apply pre-filter", params.doPreFilter);
		dlg.addNumericField("Smoothing radius (\u03C3)", params.sigma, 3);
		dlg.addNumericField("Corner response threshold (th)", params.scoreThreshold, 0);
		dlg.addEnumChoice("Subpixel localization", params.maxLocatorMethod);
		// -----------
		dlg.addNumericField("Border distance", params.border, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Minimum corner distance", params.dmin, 0);
		
		dlg.addNumericField("Corners to show (0 = show all)", Nmax, 0);
		
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		
		params.doPreFilter = dlg.getNextBoolean();
		params.sigma = Math.max(0.5, dlg.getNextNumber());	// min 0.5
		params.scoreThreshold = dlg.getNextNumber();
		params.maxLocatorMethod = dlg.getNextEnumChoice(Method.class);
		// -----------
		params.border = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		params.dmin = (int) dlg.getNextNumber();
		
		Nmax = (int) dlg.getNextNumber();
		
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}	
		return true;
	}
	
}

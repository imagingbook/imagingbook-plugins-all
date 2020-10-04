/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Corner_Detection;

import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.GradientCornerDetector;
import imagingbook.pub.corners.ShiTomasiDetector;
import imagingbook.pub.corners.subpixel.MaxLocator.Method;
import imagingbook.pub.corners.util.CornerOverlay;

/**
 * This plugin demonstrates the use of the Shi-Tomasi corner detector
 * (see {@link ShiTomasiDetector}).
 * It calculates the corner positions and shows them as a vector overlay
 * on top of the source image.
 * 
 * @see Find_Corners_Harris
 * @author WB
 * @version 2020/10/04
 */
public class Find_Corners_ShiTomasi implements PlugInFilter {
	
	private static int Nmax = 0;	// number of corners to show (0 = all)
	
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
		oly.addItems(corners);
		im.setOverlay(oly);
    }
    
	private boolean showDialog() {
		// display dialog , return false if cancelled or on error.
		GenericDialog dlg = new GenericDialog("Shi-Tomasi Corner Detector");
		dlg.addNumericField("Smoothing radius (\u03C3)", params.sigma, 3);
		dlg.addNumericField("Corner response threshold (th)", params.tH, 0);
		dlg.addChoice("Subpixel localization", 
				Enums.getEnumNames(Method.class), params.maxLocatorMethod.name()); // SubpixelMethod.None.name()
		// -----------
		dlg.addNumericField("Border distance", params.border, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Minimum corner distance", params.dmin, 0);
		
		dlg.addNumericField("Corners to show (0 = show all)", Nmax, 0);
		
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		
		params.sigma = Math.max(0.5, dlg.getNextNumber());
		params.tH = dlg.getNextNumber();
		params.maxLocatorMethod = Method.valueOf(dlg.getNextChoice());
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

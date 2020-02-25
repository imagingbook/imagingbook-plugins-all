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
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.AbstractGradientCornerDetector;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;

/**
 * This plugin demonstrates the use of the Harris corner detector
 * (see {@link HarrisCornerDetector}).
 * It calculates the corner positions and marks them in a
 * new color image.
 * 
 * @version 2020/02/25
 */
public class Find_Corners_Harris implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
	}
	
	static int nmax = 0;						// number of corners to show
	static int cornerSize = 2;					// size of cross-markers
	static Color cornerColor = Color.green;		// color of cross markers
	
	ImagePlus im;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}
		
		AbstractGradientCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		ColorProcessor R = ip.convertToColorProcessor();
		drawCorners(R, corners);
		(new ImagePlus("Harris Corners from " + im.getShortTitle(), R)).show();
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Smoothing radius (\u03C3)", params.sigma, 3);
		dlg.addNumericField("Sensitivity (\u03B1)", params.alpha, 3);
		dlg.addNumericField("Corner response threshold (th)", params.tH, 0);
		dlg.addNumericField("Border distance", params.border, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Minimum corner distance", params.dmin, 0);
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		params.sigma = Math.max(0.5, dlg.getNextNumber());
		params.alpha = Math.max(0, dlg.getNextNumber());
		params.tH = dlg.getNextNumber();
		params.border = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		params.dmin = (int) dlg.getNextNumber();
		nmax = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}	
		return true;
	}
	
	//-------------------------------------------------------------------
	
	private void drawCorners(ImageProcessor ip, List<Corner> corners) {
		ip.setColor(cornerColor);
		int n = 0;
		for (Corner c : corners) {
			c.draw(ip, cornerSize);
			n = n + 1;
			if (nmax > 0 && n >= nmax)
				break;
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

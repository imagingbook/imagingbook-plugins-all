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
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.AbstractGradientCornerDetector;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.ShiTomasiDetector;

/**
 * This plugin demonstrates the use of the Shi-Tomasi corner detector
 * (see {@link ShiTomasiDetector}).
 * It calculates the corner positions and marks them in a
 * new color image.
 * 
 * @version 2020/02/25
 */
public class Find_Corners_ShiTomasi implements PlugInFilter {
	
	static int nmax = 0;						// number of corners to show
	static int cornerSize = 2;					// size of cross-markers
	static Color cornerColor = Color.green;		// color of cross markers
	
	ImagePlus im;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	ShiTomasiDetector.Parameters params = new ShiTomasiDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}
		
		AbstractGradientCornerDetector cd = new ShiTomasiDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		ColorProcessor R = ip.convertToColorProcessor();
		drawCorners(R, corners);
		(new ImagePlus("Shi-Tomasi Corners from " + im.getShortTitle(), R)).show();
    }
    
	private boolean showDialog(ShiTomasiDetector.Parameters params) {
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Threshold", params.tH, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		params.tH = dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
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
			drawCorner(c, ip, cornerSize);
			n = n + 1;
			if (nmax > 0 && n >= nmax)
				break;
		}
	}
	
	// Moved from class 'corner'
	private void drawCorner(Corner c, ImageProcessor ip, int size) {
		int x = (int) Math.round(c.getX());
		int y = (int) Math.round(c.getY());
		ip.drawLine(x - size, y, x + size, y);
		ip.drawLine(x, y - size, x, y + size);
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

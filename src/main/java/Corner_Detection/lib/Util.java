package Corner_Detection.lib;

import java.awt.Dimension;
import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;

public abstract class Util {
	
	/**
	 * Modifies the view of the given image to the specified magnification
	 * factor and anchor position in the source image.
	 * The method fails (does nothing and returns {@code null}) if the resulting 
	 * source rectangle is not fully covered by the associated image.
	 * If successful, the view is modified and the resulting source rectangle is returned.
	 * Otherwise {@code null} is returned.
	 * 
	 * @param im the image, which must be open (displayed)
	 * @param magnification the new magnification factor (1.0 = 100%)
	 * @param xc the x-coordinate of view (upper-left corner, must be positive)
	 * @param yc the y-coordinate of view (upper-left corner, must be positive)
	 * @return the resulting source rectangle if successful, {@code null} otherwise 
	 */
	public static Rectangle setView(ImagePlus im, double magnification, int xc, int yc) {
		ImageCanvas ic = im.getCanvas();
        if (ic == null) {
        	IJ.showMessage("Image has no canvas.");
            return null;
        }
        
        Dimension d = ic.getPreferredSize();
        int dstWidth = d.width;
        int dstHeight = d.height;
        
        int imgWidth = im.getWidth();
        int imgHeight = im.getHeight();
        
        if (xc < 0 || yc < 0) {
        	throw new IllegalArgumentException("anchor coordinates may not be negative!");
        }
        
        if (magnification <= 0.001) {
            throw new IllegalArgumentException("magnification value must be positive!");
        }
        
        // calculate size of the new source rectangle
        int srcWidth  = (int) Math.ceil(dstWidth / magnification); // check!!
        int srcHeight = (int) Math.ceil(dstHeight / magnification);
        
        if (xc + srcWidth > imgWidth || yc + srcHeight > imgHeight) {
        	// source rectangle does not fully fit into source image
        	return null;
        }
        
        Rectangle srcRect = new Rectangle(xc, yc, srcWidth, srcHeight);
        ic.setSourceRect(srcRect);
        im.repaintWindow();
        
		return srcRect;
		
	}

	
	// Adapted from gdsc.core.ij.Utils 
	// http://www.sussex.ac.uk/gdsc/intranet/microscopy/UserSupport/AnalysisProtocol/imagej/gdsc_plugins
	// https://github.com/aherbert/gdsc-core/blob/master/src/main/java/uk/ac/sussex/gdsc/core/ij/ImageJUtils.java

	/*----------------------------------------------------------------------------- 
	 * GDSC SMLM Software
	 * 
	 * Copyright (C) 2016 Alex Herbert
	 * Genome Damage and Stability Centre
	 * University of Sussex, UK
	 * 
	 * This program is free software; you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation; either version 3 of the License, or
	 * (at your option) any later version.
	 *---------------------------------------------------------------------------*/
	
	/**
     * Set the current source rectangle to centre the view on the given coordinates
     * 
     * Adapted from ij.gui.ImageCanvas.adjustSourceRect(double newMag, int x, int y)
     * 
     * @param imp
     *            The image
     * @param newMag
     *            The new magnification (set to zero to use the current magnification)
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     */
    private static void adjustSourceRect(ImagePlus imp, double newMag, int x, int y) {
        ImageCanvas ic = imp.getCanvas();
        if (ic == null)
            return;
        Dimension d = ic.getPreferredSize();
        int dstWidth = d.width, dstHeight = d.height;
        int imageWidth = imp.getWidth(), imageHeight = imp.getHeight();
        if (newMag <= 0)
            newMag = ic.getMagnification();
        int w = (int) Math.round(dstWidth / newMag);
        if (w * newMag < dstWidth)
            w++;
        int h = (int) Math.round(dstHeight / newMag);
        if (h * newMag < dstHeight)
            h++;
        //x = ic.offScreenX(x);
        //y = ic.offScreenY(y);
        Rectangle r = new Rectangle(x - w / 2, y - h / 2, w, h);
        if (r.x < 0)
            r.x = 0;
        if (r.y < 0)
            r.y = 0;
        if (r.x + w > imageWidth)
            r.x = imageWidth - w;
        if (r.y + h > imageHeight)
            r.y = imageHeight - h;
        ic.setSourceRect(r);
        ic.setMagnification(newMag);
        ic.repaint();
    }

}

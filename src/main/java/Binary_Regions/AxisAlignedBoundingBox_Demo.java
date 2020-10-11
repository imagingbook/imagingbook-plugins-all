/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Binary_Regions;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.hulls.AxisAlignedBoundingBox;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

/**
 * This plugin creates a binary region segmentation, calculates 
 * the center and major axis and subsequently the major axis-aligned
 * bounding box for each region.
 */
public class AxisAlignedBoundingBox_Demo implements PlugInFilter {
	
	static Color CenterColor = Color.magenta;
	static Color BoundingBoxColor = Color.blue;
	
	private String title = null;
	
	public int setup(String arg, ImagePlus im) { 
		title = im.getShortTitle();
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	   	
		RegionContourLabeling segmenter = new RegionContourLabeling((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions();
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}
		
		ColorProcessor cp = ip.convertToColorProcessor();
		cp.add(128);	// brighten
		
		
		for (BinaryRegion r: regions) {
			int uc = (int) Math.round(r.getXc());
			int vc = (int) Math.round(r.getYc());
			Point[] box = (new AxisAlignedBoundingBox(r)).getCornerPoints();
			//double[][] box = getAxisAlignedBoundingBox(r);
			drawCenter(cp,  uc,  vc);
			drawBox(cp, box);
		}

		(new ImagePlus(title + "-aligned-bb", cp)).show();
	}
	
	
	private void drawBox(ImageProcessor ip, Point[] box) {
		ip.setColor(BoundingBoxColor);
		ip.setLineWidth(1);
		drawLine(ip, box[0], box[1]);
		drawLine(ip, box[1], box[2]);
		drawLine(ip, box[2], box[3]);
		drawLine(ip, box[3], box[0]);
	}
	
	private void drawLine(ImageProcessor ip, Point p0, Point p1) {
		int u0 = (int) Math.round(p0.getX());
		int v0 = (int) Math.round(p0.getY());
		int u1 = (int) Math.round(p1.getX());
		int v1 = (int) Math.round(p1.getY());
		ip.drawLine(u0, v0, u1, v1);	
	}
	
	void drawCenter(ImageProcessor ip, int uc, int vc) {
		ip.setColor(CenterColor);
		ip.setLineWidth(1);
		ip.drawRect(uc - 2, vc - 2, 5, 5);
	}
}

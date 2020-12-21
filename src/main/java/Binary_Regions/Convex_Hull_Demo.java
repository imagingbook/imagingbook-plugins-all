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
import java.awt.geom.Line2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.regions.SegmentationRegionContour;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;
import imagingbook.pub.geometry.hulls.ConvexHull;

/**
 * This plugin demonstrates the use of the {@link ConvexHull} class.
 * It performs region segmentation, calculates the convex hull
 * for each region found and then draws the result into a new color
 * image.
 * Requires a binary (segmented) image.
 * 
 * @author W. Burger
 * @version 2020/12/17
 * 
 */
public class Convex_Hull_Demo implements PlugInFilter {
	
	static Color ConvexHullColor = Color.blue;
	
	private String title = null;
	
	public int setup(String arg, ImagePlus im) {
		title = im.getShortTitle();
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		SegmentationRegionContour segmenter = new SegmentationRegionContour((ByteProcessor) ip);
//		RegionLabeling segmenter = new DepthFirstLabeling((ByteProcessor) ip);
		
		List<BinaryRegion> regions = segmenter.getRegions();
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}
		
		ColorProcessor cp = ip.convertToColorProcessor();
		cp.add(128);
		
		for (BinaryRegion r: regions) {
			//ConvexHull hull = new ConvexHull(r);					// takes all region points
			ConvexHull hull = new ConvexHull(r.getOuterContour());	// takes only outer contour points
			
			Line2D[] segments = hull.getSegments();
			drawHull(cp, segments);
		}

		(new ImagePlus(title + "-convex-hulls", cp)).show();
	}
	
	// ----------------------------------------------------
	
	private void drawHull(ImageProcessor ip, Line2D[] segments) {
		for (Line2D line : segments) {
			drawSegment(ip, Point.create(line.getP1()), Point.create(line.getP2()));
		}
	}

	private void drawSegment(ImageProcessor ip, Point p1, Point p2) {
		int x1 = (int) Math.round(p1.getX());
		int y1 = (int) Math.round(p1.getY());
		int x2 = (int) Math.round(p2.getX());
		int y2 = (int) Math.round(p2.getY());
		ip.drawLine(x1, y1, x2, y2);
	}

}

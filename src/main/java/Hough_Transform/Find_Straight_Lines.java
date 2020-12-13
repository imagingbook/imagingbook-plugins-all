/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Hough_Transform;

import java.awt.Color;
import java.awt.geom.Path2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Toolbar;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.hough.HoughTransformLines;
import imagingbook.pub.hough.lines.HoughLine;

/** 
 * This ImageJ plugin demonstrates the use of the {@link HoughTransformLines}
 * class for detecting straight lines in images.
 * It expects an input image with background = 0 and foreground (contour) 
 * pixels with values &gt; 0.
 * A vector overlay is used to display the detected lines.
 * 
 * TODO: Show marker as overlay. 
 * TODO: Use CustomOverlay?
 * 
 * @author W. Burger
 * @version 2020/12/13
 */

public class Find_Straight_Lines implements PlugInFilter {

	static int MaxLines = 5;			// number of strongest lines to be found
	static int MinPointsOnLine = 50;	// min. number of points on each line

	static boolean ShowAccumulator = true;
	static boolean ShowExtendedAccumulator = false;
	static boolean ShowAccumulatorPeaks = false;
	static boolean ListStrongestLines = false;
	static boolean ShowLines = true;
	static boolean InvertOriginal = true;
	
	static double  LineWidth = 1.0;
	static Color   DefaultLineColor = Color.magenta;
	static boolean UsePickedColor = false;
	static boolean ShowReferencePoint = true;
	static Color   ReferencePointColor = Color.green;

	ImagePlus imp;	

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Binary (edge) image required!");
			return;
		}

		HoughTransformLines.Parameters params = new HoughTransformLines.Parameters();

		if (!showDialog(params)) //dialog canceled or error
			return; 

		// compute the Hough Transform and retrieve the strongest lines:
		HoughTransformLines ht = new HoughTransformLines(ip, params);
		HoughLine[] lines = ht.getLines(MinPointsOnLine, MaxLines);

		if (lines.length == 0) {
			IJ.log("No lines detected - check the input image and parameters!");
		}

		if (ShowAccumulator){
			FloatProcessor accIp = ht.getAccumulatorImage();
			(new ImagePlus("HT of " + imp.getTitle(), accIp)).show();
		}

		if (ShowExtendedAccumulator){
			FloatProcessor accEx = ht.getAccumulatorImageExtended();
			(new ImagePlus("accumExt of " + imp.getTitle(), accEx)).show();
		}

		if (ShowAccumulatorPeaks) {
			FloatProcessor maxIp = ht.getAccumulatorMaxImage();
			(new ImagePlus("Maxima of " + imp.getTitle(), maxIp)).show();
		}

		if (ListStrongestLines) {
			for (int i = 0; i < lines.length; i++) {
				IJ.log(i + ": " + lines[i].toString());
			}
		}

		if (ShowLines) {
			Color lineColor = DefaultLineColor;
			ColorProcessor lineIp = ip.convertToColorProcessor();
			if (InvertOriginal) lineIp.invert();
			if (UsePickedColor) {
				lineColor = Toolbar.getForegroundColor();
			}

			Overlay oly = new Overlay();
			
			for (HoughLine hl : lines){
				//hl.draw(lineIp, LineWidth);	// was brute-force painting
				Roi roi = hl.makeLine();
				roi.setStrokeColor(lineColor);
				oly.add(roi);
			}

			if (ShowReferencePoint) {
//				lineIp.setColor(ReferencePointColor);
//				int ur = (int) Math.round(ht.getXref());
//				int vr = (int) Math.round(ht.getYref());
//				drawCross(lineIp, ur, vr, 2);
				
				double markerSize = 2.0;
				double xc = ht.getXref();
				double yc = ht.getYref();
				Path2D path = new Path2D.Double();
				path.moveTo(xc - markerSize, yc);
				path.lineTo(xc + markerSize, yc);
				path.moveTo(xc, yc - markerSize);
				path.lineTo(xc, yc + markerSize);
				ShapeRoi cross = new ShapeRoi(path);
				cross.setStrokeWidth(0.3);
				cross.setStrokeColor(ReferencePointColor);
				oly.add(cross);
			}

			ImagePlus him = new ImagePlus(imp.getShortTitle()+"-lines", lineIp);
			oly.translate(0.5, 0.5);	// shift to run through pixel centers
			him.setOverlay(oly);
			him.show();
		}
	}

	private void drawCross(ImageProcessor ip, int uu, int vv, int size) {
		ip.drawLine(uu - size, vv, uu + size, vv);
		ip.drawLine(uu, vv - size, uu, vv + size);
	}

	// -----------------------------------------------------------------

	private boolean showDialog(HoughTransformLines.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Hough Transform (lines)");
		dlg.addNumericField("Axial steps", params.nAng, 0);
		dlg.addNumericField("Radial steps", params.nRad, 0);
		dlg.addNumericField("Max. number of lines to show", MaxLines, 0);
		dlg.addNumericField("Min. number of points per line", MinPointsOnLine, 0);
		dlg.addCheckbox("Show accumulator", ShowAccumulator);
		dlg.addCheckbox("Show extended accumulator", ShowExtendedAccumulator);
		dlg.addCheckbox("Show accumulator peaks", ShowAccumulatorPeaks);
		dlg.addCheckbox("List strongest lines", ListStrongestLines);
		dlg.addCheckbox("Show lines", ShowLines);
		dlg.addNumericField("Line width", LineWidth, 1);
		dlg.addCheckbox("Draw with picked color", UsePickedColor);
		dlg.addCheckbox("Show reference point", ShowReferencePoint);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		params.nAng = (int) dlg.getNextNumber();
		params.nRad = (int) dlg.getNextNumber();
		MaxLines = (int) dlg.getNextNumber();
		MinPointsOnLine = (int) dlg.getNextNumber();
		ShowAccumulator = dlg.getNextBoolean();
		ShowExtendedAccumulator = dlg.getNextBoolean();
		ShowAccumulatorPeaks = dlg.getNextBoolean();
		ListStrongestLines = dlg.getNextBoolean();
		ShowLines = dlg.getNextBoolean();
		LineWidth = dlg.getNextNumber();
		UsePickedColor = dlg.getNextBoolean();
		ShowReferencePoint = dlg.getNextBoolean();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}

}

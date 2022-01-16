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
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.hough.HoughTransformLines;
import imagingbook.pub.hough.lines.HoughLine;
import imagingbook.pub.hough.lines.HoughLineOverlay;

/** 
 * This ImageJ plugin demonstrates the use of the {@link HoughTransformLines}
 * class for detecting straight lines in images.
 * It expects an input image with background = 0 and foreground (contour) 
 * pixels with values &gt; 0.
 * A vector overlay is used to display the detected lines.
 * 
 * @author W. Burger
 * @version 2020/12/13
 */
public class Hough_Lines_Demo implements PlugInFilter {

	static int MaxLines = 5;			// number of strongest lines to be found
	static int MinPointsOnLine = 50;	// min. number of points on each line

	static boolean ShowAccumulator = true;
	static boolean ShowExtendedAccumulator = false;
	static boolean ShowAccumulatorPeaks = false;
	static boolean ListStrongestLines = false;
	static boolean ShowLines = true;
	static boolean InvertOriginal = true;
	
	static double  LineWidth = 0.5;
	static Color   LineColor = Color.magenta;
	
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
			ColorProcessor lineIp = ip.convertToColorProcessor();
			if (InvertOriginal) lineIp.invert();

			HoughLineOverlay oly = new HoughLineOverlay(ip.getWidth(), ip.getHeight());
			oly.strokeColor(LineColor);
			oly.strokeWidth(LineWidth);
			oly.addItems(Arrays.asList(lines));

			if (ShowReferencePoint) {
				oly.strokeColor(ReferencePointColor);
				oly.strokeWidth(1.0);
				oly.markPoint(ht.getXref(), ht.getYref(), ReferencePointColor);
			}

			ImagePlus him = new ImagePlus(imp.getShortTitle()+"-lines", lineIp);
			him.setOverlay(oly);
			him.show();
		}
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
		ShowReferencePoint = dlg.getNextBoolean();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Geometric_Operations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/**
 * ImageJ plugin for configurable affine image transformation.
 * TODO: Polish the matrix dialogue input.
 * 
 * @author WB
 * @version 2015/08/05
 *
 */
public class Transform_Affine_Matrix implements PlugInFilter {
	
 
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	if (!showDialog())
    		return;
    	
//        displayValues();
        
        double a11 = matrixValues[0][0];
        double a12 = matrixValues[0][1];
        double a13 = matrixValues[0][2];
        double a21 = matrixValues[1][0];
        double a22 = matrixValues[1][1];
        double a23 = matrixValues[1][2];
        
        AffineMapping imap = new AffineMapping(a11, a12, a13, a21, a22, a23).getInverse();
        imap.applyTo(ip, InterpolationMethod.Bicubic);
    }
    
    // Dialog example taken from http://rsbweb.nih.gov/ij/plugins/download/Dialog_Grid_Demo.java
    
    String[][] matrixNames = {
    		{ "a11", "a12", "a13" },
    		{ "a21", "a22", "a23" }};
    
    double[][] matrixValues = {
    		{ 1, 0, 0 },
    		{ 0, 1, 0 }};

	
	TextField[] tf = new TextField[matrixValues[0].length * matrixValues.length];
//	double[] value = new double[gridSize];

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Enter affine transformation matrix");
		gd.addPanel(makePanel(gd));
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		getValues();
		return true;
	}

	private Panel makePanel(GenericDialog gd) {
		int gridWidth = matrixValues[0].length;
		int gridHeight = matrixValues.length;
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(gridHeight, gridWidth * 2));
		int i = 0;
		for (int row = 0; row < gridHeight; row++) {
			for (int col = 0; col < gridWidth; col++) {
				tf[i] = new TextField("" + matrixValues[row][col]);
				panel.add(tf[i]);
				panel.add(new Label(matrixNames[row][col]));
				i++;
			}
		}
		return panel;
	}
	
	private void getValues() {
		int gridWidth = matrixValues[0].length;
		int gridHeight = matrixValues.length;
		int i = 0; 
		for (int r = 0; r < gridHeight; r++) {
			for (int c = 0; c < gridWidth; c++) {
			String s = tf[i].getText();
			matrixValues[r][c] = getValue(s);
			i++;
			}
		}
	}
	
//	void displayValues() {
//		int i = 0; 
//		for (int r = 0; r < gridHeight; r++) {
//			for (int c = 0; c < gridWidth; c++) {
//				IJ.log(i + "  " + fieldValues[r][c]);
//				i++;
//			}
//		}
//	}

	private double getValue(String valueAsText) {
		Double d;
		try {
			d = new Double(valueAsText);
		} catch (NumberFormatException e) {
			d = null;
		}
		return (d == null) ? Double.NaN : d.doubleValue();
	}

}

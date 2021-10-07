/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Geometric_Operations;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageMapper;
import imagingbook.pub.geometry.mappings.linear.AffineMapping2D;

/**
 * ImageJ plugin for configurable affine image transformation.
 * 
 * @author WB
 * @version 2021/10/07
 *
 */
public class Map_Affine_Matrix implements PlugInFilter {

	private static String[][] elementNames = {
			{ "a00", "a01", "a02" },
			{ "a10", "a11", "a12" }};

	private static double[][] A = {
			{ 1, 0, 0 },
			{ 0, 1, 0 }};
	
	private TextField[] tf = new TextField[A[0].length * A.length];
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!showDialog()) {
			return;
		}
		AffineMapping2D imap = new AffineMapping2D(A).getInverse();
		new ImageMapper(imap).map(ip);
	}


	// Dialog example taken from http://rsbweb.nih.gov/ij/plugins/download/Dialog_Grid_Demo.java

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Enter affine transformation matrix");
		gd.addPanel(makePanel(gd));
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		getValues();
		return true;
	}

	private Panel makePanel(GenericDialog gd) {
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(A.length, A[0].length * 2));
		int i = 0;
		for (int row = 0; row < A.length; row++) {
			for (int col = 0; col < A[0].length; col++) {
				tf[i] = new TextField("" + A[row][col]);
				panel.add(tf[i]);
				panel.add(new Label(elementNames[row][col]));
				i++;
			}
		}
		return panel;
	}

	private void getValues() {
		int i = 0; 
		for (int r = 0; r < A.length; r++) {
			for (int c = 0; c < A[0].length; c++) {
				String s = tf[i].getText();
				A[r][c] = getValue(s);
				i++;
			}
		}
	}

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

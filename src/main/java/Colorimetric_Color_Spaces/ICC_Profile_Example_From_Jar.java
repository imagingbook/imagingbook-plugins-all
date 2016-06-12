/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Colorimetric_Color_Spaces;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.lib.math.Matrix;
import imagingbook.lib.util.ResourceUtils;


/**
 * This plugin lists a user-selected ICC profile that is retrieved
 * from a Java resource.
 * 
 * @author W. Burger
 *
 */
public class ICC_Profile_Example_From_Jar implements PlugIn {
	
	static {
		IjLogStream.redirectSystem();
	}
	
	static final String ProfileDirectory = "sample-icc-profiles/";

	String[] choices;
	String theChoice;
	
	public void run(String arg) {
		Class<?> clazz = imagingbook.lib.color.CssColor.class;
		List<String> profileList = new ArrayList<String>();
		Path[] paths = ResourceUtils.listResources(clazz, ProfileDirectory);
		for (Path p : paths) {
			String name = p.getFileName().toString();
			profileList.add(name);
		}
		
		Collections.sort(profileList);
		choices = profileList.toArray(new String[0]);
		
		if (!showDialog())
			return;
		
		System.out.println("selected ICC profile: " + theChoice);
		
		ICC_Profile profile = null;
		try {
//			InputStream strm = ResourceUtils.getResourceStream(clazz, ProfileDirectory + "/" + theChoice);
			InputStream strm = clazz.getResourceAsStream(ProfileDirectory + theChoice);
			if (strm != null)
				profile = ICC_Profile.getInstance(strm);
		} catch (IOException e) { }
		
		System.out.println("profile = " + profile);

		ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(profile);
		int nComp = iccColorSpace.getNumComponents();
		if (nComp != 3) {
			IJ.error("Color space must have 3 components, this one has " + nComp);
			return;
		}
		
		IJ.log("color space = " + iccColorSpace);
		IJ.log("color space type = " + iccColorSpace.getType());
		IJ.log("color space ncomp = " + iccColorSpace.getNumComponents());
		
		
		// specify a device-specific color:
		float[] deviceColor = {0.77f, 0.13f, 0.89f};
		//float[] deviceColor = {0.0f, 0.0f, 0.0f};
		IJ.log("device color = " + Matrix.toString(deviceColor));
		
		// convert to sRGB:
		float[] sRGBColor = iccColorSpace.toRGB(deviceColor);
		IJ.log("sRGB = " + Matrix.toString(sRGBColor));
		
		// convert to (D50-based) XYZ:
		float[] XYZColor = iccColorSpace.toCIEXYZ(deviceColor);
		IJ.log("XYZ = " + Matrix.toString(XYZColor));
				
		deviceColor = iccColorSpace.fromRGB(sRGBColor);
		IJ.log("device color direct (check) = " + Matrix.toString(deviceColor));
		
		deviceColor = iccColorSpace.fromCIEXYZ(XYZColor);
		IJ.log("device color via XYZ (check) = " + Matrix.toString(deviceColor));
		
		// list sRGB Values:
		for (int ri = 0; ri <= 10; ri++) {
			for (int gi = 0; gi <= 10; gi++) {
				for (int bi = 0; bi <= 10; bi++) {
					float[] devCol = {ri * 0.1f, gi * 0.1f, bi * 0.1f};
					float[] sRGB = iccColorSpace.toRGB(devCol);
					float[] devColCheck = iccColorSpace.fromRGB(sRGB);
					IJ.log(Matrix.toString(devCol) + " -> " + Matrix.toString(sRGB) + " -> " 
							+ Matrix.toString(devColCheck) + warning(devCol, devColCheck));
				}
			}
		}
	}
	
	String warning(float[] col1, float[] col2) {
		float t = 0.05f;
		for (int i=0; i<col1.length; i++) {
			if (Math.abs(col1[i] - col2[i]) > t)
				return " ***";
		}
		return "";
	}
	
	boolean showDialog() {
		GenericDialog gd = new GenericDialog(ICC_Profile_Example_From_Jar.class.getSimpleName());
		gd.addMessage("Select an ICC profile:");
		gd.addChoice("Profile:", choices, choices[0]);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		int mi = gd.getNextChoiceIndex();
		theChoice = choices[mi];
		
		return true;
	}
	

}

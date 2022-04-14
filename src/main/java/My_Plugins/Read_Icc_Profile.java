package My_Plugins;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

import ij.IJ;
import ij.plugin.PlugIn;
import imagingbook.core.resource.NamedResource;
import imagingbook.lib.color.IccProfile;
import imagingbook.lib.math.Matrix;

public class Read_Icc_Profile implements PlugIn {

	@Override
	public void run(String arg) {
		NamedResource res = IccProfile.AdobeRGB1998;
		IJ.log("URL = " + res.getURL());
		IJ.log("inside JAR = " + res.isInsideJar());
		
		ICC_Profile profile = null;
		InputStream strm = res.getStream();		// this must work when resource is in JAR!
		try {
			if (strm != null)
				profile = ICC_Profile.getInstance(strm);
		} catch (IOException e) { }
		
		IJ.log("profile = " + profile);
		
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
	
	private String warning(float[] col1, float[] col2) {
		float t = 0.05f;
		for (int i = 0; i < col1.length; i++) {
			if (Math.abs(col1[i] - col2[i]) > t)
				return " ***";
		}
		return "";
	}

}

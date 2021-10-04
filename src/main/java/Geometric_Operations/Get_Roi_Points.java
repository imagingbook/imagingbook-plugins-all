package Geometric_Operations;

import java.awt.Polygon;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;

/**
 * ImageJ plugin demonstrating the extraction of vertex points 
 * from a user-selected polygon ROI (region of interest).
 * Two different versions are shown using
 * integer coordinates and floating-point coordinates, respectively.
 * Notice that the ROI is obtained from the associated {@link ImagePlus} instance 
 * (to which a reference is stored in the {@link #setup(String, ImagePlus)} method) 
 * and not from the {@link ImageProcessor} {@code ip}.
 * @author WB
 *
 */
public class Get_Roi_Points implements PlugInFilter {
	
	ImagePlus im = null;
	
	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_ALL + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		
		Roi roi = im.getRoi();	
		if (!(roi instanceof PolygonRoi)) {
			IJ.error("Polygon selection required!");
			return;
		}
		
		IJ.log("ROI integer coordinates:");
		Polygon pgnI = roi.getPolygon();
		Pnt2d[] ptsI = new Pnt2d[pgnI.npoints];
		for (int i = 0; i < pgnI.npoints; i++) {
			ptsI[i] = Pnt2d.from(pgnI.xpoints[i], pgnI.ypoints[i]);
			IJ.log(i + ": " + ptsI[i].toString());
		}
		// alternative: ptsI = RoiUtils.getPolygonPointsInt(roi);
		
		IJ.log("ROI float coordinates:");
		FloatPolygon pgnF = roi.getFloatPolygon();
		Pnt2d[] ptsF = new Pnt2d[pgnF.npoints];
		for (int i = 0; i < pgnF.npoints; i++) {
			ptsF[i] = Pnt2d.from(pgnF.xpoints[i], pgnF.ypoints[i]);
			IJ.log(i + ": " + ptsF[i].toString());
		}
		// alternative: ptsF = RoiUtils.getPolygonPointsFloat(roi);
	}
	
}
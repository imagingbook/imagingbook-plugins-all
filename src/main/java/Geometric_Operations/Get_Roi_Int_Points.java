package Geometric_Operations;

import java.awt.Polygon;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;

public class Get_Roi_Int_Points implements PlugInFilter {
	
	ImagePlus im = null;
	
	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_ALL + ROI_REQUIRED;
	}

	public void run(ImageProcessor source) {
		
		Roi roi = im.getRoi();
		
		if (!(roi instanceof PolygonRoi)) {
			IJ.error("Polygon selection required!");
			return;
		}
		
		Polygon pgn = roi.getPolygon();
		
		// copy polygon vertices to a point array:
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		
		// alternative:
		// pts = RoiUtils.getPolygonPointsInt(roi);
		
		// list all points
		for (int i = 0; i < pts.length; i++) {
			IJ.log(i + ": " + pts[i].toString());
		}
		
		// ... use the integer points in pts
		
	}
	
}
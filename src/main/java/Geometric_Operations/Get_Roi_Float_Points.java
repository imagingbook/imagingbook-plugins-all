package Geometric_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;

public class Get_Roi_Float_Points implements PlugInFilter {
	
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
		
		FloatPolygon pgn = roi.getFloatPolygon();
		
		// copy polygon vertices to a point array:
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		
		// alternative:
		// pts = RoiUtils.getPolygonPointsFloat(roi);
		
		for (int i = 0; i < pts.length; i++) {
			IJ.log(i + ": " + pts[i].toString());
		}
			
		// ... use the float points in pts
		
	}
	
}
package Geometric_Operations;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;

public class Get_Roi_Points implements PlugInFilter {
	
	ImagePlus im = null;
	
	public int setup(String args, ImagePlus im) {
		this.im = im;		// keep a reference to \Code{im}\label{pr:RoiPolygonPointsDemo010}
		return DOES_ALL + ROI_REQUIRED;
	}

	public void run(ImageProcessor source) {
		
		Roi roi = im.getRoi();
		
		if (!(roi instanceof PolygonRoi)) {
			IJ.error("Polygon selection required!");
			return;
		}
		
		Polygon poly = roi.getPolygon();
		
		// copy polygon vertices to a point array:
		Pnt2d[] pts = new Pnt2d[poly.npoints];
		for (int i = 0; i < poly.npoints; i++) {
			pts[i] = Pnt2d.from(poly.xpoints[i], poly.ypoints[i]);
		}
		
		// ... use the ROI points in \Code{pts}
		
	}
	
}
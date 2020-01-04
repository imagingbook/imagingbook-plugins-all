package _Point_Fitting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.fitting.AffineFit2D;
import imagingbook.pub.geometry.fitting.LinearFit2D;
import imagingbook.pub.geometry.fitting.ProcrustesFit;

/**
 * This IJ plugin creates a set of K random points, which are
 * displayed in the first frame of a 2-image stack.
 * The point set is transformed by a (fixed) affine transformation,
 * the result is shown in frame 2.
 * All point coordinates are integers, the transformed coordinates are rounded
 * to their closest values.
 * Then a linear fit is found between the two point sets, using either
 * (a) an affine fitter or
 * (b) a Procrustes fitter.
 * The correspondence between the point sets is known.
 * 
 * @author WB
 *
 */
public class Affine_Transform_Fitting_Demo implements PlugIn {
	
	static int W = 400;
	static int H = 400;
	static int K = 200;		// number of points
	
	static final String title = Affine_Transform_Fitting_Demo.class.getSimpleName();
	
	private static final String[] FittingMethods = {"Affine Least Squares", "Procrustes"};
	private static int theMethod = 0;
	
	
//	static double[][] A = 	// affine 2D transformation
//		{{ 0.013,  1.088,  18.688 },
//		 {-1.000, -0.050, 127.500 }};
	
	static double a = 10 * 2 * Math.PI / 360;
	static double ca = Math.cos(a);
	static double sa = Math.sin(a);
	static double S = 1.05;
	
//	static double[][] A = 	// affine 2D transformation
//		{{ S * ca, S * -sa, 18.688 },
//		 { S * sa, S *  ca, -7.123 }};
	
	static double[][] A = 	// affine 2D transformation
	{{ 1, 0, 0 },
	 { 0, 1, 0 }};

	public void run(String arg) {
		if (!runDialog())
			return;
			
		IJ.log("Fitter used: " + FittingMethods[theMethod]);
		IJ.log("A (original) = \n" + Matrix.toString(A));
		
		// create the image stack
		ImageStack stack = new ImageStack(W, H);
		ImageProcessor ip1 = new ByteProcessor(W, H);
		ImageProcessor ip2 = new ByteProcessor(W, H);
		
		stack.addSlice(ip1);
		stack.addSlice(ip2);
		
		// create K random points and place in ip1:
		Random rg = new Random();
		List<Point> points1 = new ArrayList<>();
		for (int i = 0; i < K; i++) {
			int u = W/4 + rg.nextInt(W/2);
			int v = H/4 + rg.nextInt(H/2);
			points1.add(Point.create(u, v));
			ip1.putPixel(u, v, 255);
		}
		
		// transform points:
		List<Point> points2 = new ArrayList<>();
		for (Point p1 : points1) {
			double[] p2 = Matrix.multiply(A, Matrix.toHomogeneous(new double[] {p1.getX(), p1.getY()}));
			int u = (int) Math.round(p2[0]);
			int v = (int) Math.round(p2[1]);
			points2.add(Point.create(u, v));
			ip2.putPixel(u, v, 255);
		}
		
		new ImagePlus("Stack", stack).show();
		
		// select a fitter
//		AffineFit fitter = new AffineFit();
//		ProcrustesFit fitter = new ProcrustesFit();
		
		LinearFit2D fitter = null;
		switch (theMethod) {
		case 0: fitter = new AffineFit2D(); break;
		case 1: fitter = new ProcrustesFit(); break;
		}
				
		fitter.fit(points1, points2); // least-squares fit
		
		double[][] Ae = fitter.getTransformationMatrix().getData();
		IJ.log("A (estimated) = \n" + Matrix.toString(Ae));
		
		//IJ.log("S = " + fitter.getScale());
		IJ.log(String.format("e = %.6f", fitter.getError()));
	}
	
	// ---------------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(title);
		gd.addChoice("Select fitting method", 
				FittingMethods, FittingMethods[theMethod]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		theMethod = gd.getNextChoiceIndex();
		return true;
	}

}

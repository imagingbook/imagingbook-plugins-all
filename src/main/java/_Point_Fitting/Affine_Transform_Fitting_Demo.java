package _Point_Fitting;

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
	
	static boolean allowTranslation = true;
	static boolean allowScaling = true;
	static boolean forceRotation = true;
	
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
	
	static double[][] A = 	// affine 2D transformation
		{{ S * ca, S * -sa, 18.688 },
		 { S * sa, S *  ca, -7.123 }};
	
//	static double[][] A = 	// affine 2D transformation
//	{{ 1, 0, 0 },
//	 { 0, 1, 0 }};

	public void run(String arg) {
		if (!runDialog())
			return;
			
		IJ.log("Fitter used: " + FittingMethods[theMethod]);
		IJ.log("A (original) = \n" + Matrix.toString(A));
		
		// create the image stack
		ImageStack stack = new ImageStack(W, H);
		ImageProcessor ipP = new ByteProcessor(W, H);
		ImageProcessor ipQ = new ByteProcessor(W, H);
		stack.addSlice(ipP);
		stack.addSlice(ipQ);
		
		// create K random points and draw them in ipP:
		Random rg = new Random();
		Point[] P = new Point[K];
		for (int i = 0; i < K; i++) {
			int u = W/4 + rg.nextInt(W/2);
			int v = H/4 + rg.nextInt(H/2);
			P[i] = Point.create(u, v);
			ipP.putPixel(u, v, 255);
		}
		
		// transform points: TODO: use an affine mapping instead!
		Point[] Q = new Point[K];
		for (int i = 0; i < K; i++) {
			double[] q = Matrix.multiply(A, Matrix.toHomogeneous(Q[i].toArray()));
			// we round coordinates to simulate a discrete grid transformation:
			int u = (int) Math.round(q[0]);
			int v = (int) Math.round(q[1]);
			Q[i] = Point.create(u, v);
			ipQ.putPixel(u, v, 255);
		}
		
		new ImagePlus("Stack", stack).show();
		
		// select a fitter:
		LinearFit2D fitter = null;
		switch (theMethod) {
		case 0: fitter = new AffineFit2D(P, Q); 
				break;
		case 1: fitter = new ProcrustesFit(P, Q, allowTranslation, allowScaling, forceRotation); 
				break;
		}
		
		double[][] A = fitter.getTransformationMatrix().getData();
		IJ.log("A (estimated) = \n" + Matrix.toString(A));
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

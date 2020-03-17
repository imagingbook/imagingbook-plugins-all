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
 * @version 2020/03/17
 *
 */
public class Affine_Transform_Fitting_Demo implements PlugIn {
	
	static int W = 400;
	static int H = 400;
	static int K = 200;			// number of points
	
	static double dX = 18.688;
	static double dY = -7.123;
	
	static double S = 1.05;		// uniform scale factor
	static double alpha = 20;	// rotation angle (in degrees)
	static double sigma = 0.0;	// amount of additive Gaussian position noise 
	
	static boolean allowTranslation = true;
	static boolean allowScaling = true;
	static boolean forceRotation = true;
	
	static final String title = Affine_Transform_Fitting_Demo.class.getSimpleName();
	
	private static final String[] FittingMethods = {"Affine Least Squares", "Procrustes"};
	private static int theMethod = 0;
	
	// -------------------------------------------------------------

	public void run(String arg) {
		if (!runDialog())
			return;
			
		IJ.log("Fitter used: " + FittingMethods[theMethod]);
		
		double a = alpha * Math.PI / 360;
		double[][] A = 		// affine 2D transformation matrix
			{{ S * Math.cos(a), S * -Math.sin(a), dX },
			 { S * Math.sin(a), S *  Math.cos(a), dY }};
		
		IJ.log("\nA (original) = \n" + Matrix.toString(A));
		
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
		
		// transform points P -> Q
		Point[] Q = new Point[K];
		for (int i = 0; i < K; i++) {
			double[] q = Matrix.multiply(A, Matrix.toHomogeneous(P[i].toArray()));
			// we add Gaussian noise and round to simulate a discrete grid transformation:
			int u = (int) Math.round(q[0] + rg.nextGaussian() * sigma);
			int v = (int) Math.round(q[1] + rg.nextGaussian() * sigma);
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
		
		double[][] Af = fitter.getTransformationMatrix().getData();
		IJ.log("\nA (estimated) = \n" + Matrix.toString(Af));
		IJ.log(String.format("\nRMS error: \u03B5 = %.6f", Math.sqrt(fitter.getError())));
	}
	
	// -------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(title);
		gd.addNumericField("Image width (W)", W, 0);
		gd.addNumericField("Image height (H)", H, 0);
		gd.addNumericField("Number of points (K)", K, 0);
		gd.addNumericField("Translation in x (dX)", dX, 2);
		gd.addNumericField("Translation in x (dY)", dY, 2);
		gd.addNumericField("Rotation angle (degrees)", alpha, 2);
		gd.addNumericField("Uniform scale factor (s)", S, 2);
		gd.addNumericField("Position noise (\u03C3)", sigma, 2);
		gd.addChoice("Fitting method", FittingMethods, FittingMethods[theMethod]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		W = (int) gd.getNextNumber();
		H = (int) gd.getNextNumber();
		K = (int) gd.getNextNumber();
		dX = gd.getNextNumber();
		dY = gd.getNextNumber();
		alpha = gd.getNextNumber();
		S = gd.getNextNumber();
		sigma = gd.getNextNumber();
		theMethod = gd.getNextChoiceIndex();
		return true;
	}

}

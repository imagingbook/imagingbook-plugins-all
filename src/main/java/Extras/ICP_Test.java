package Extras;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.RealMatrix;

import Extras.geom.points.IterativeClosestPointMatcher;
import Extras.geom.points.IterativeClosestPointMatcher.IterationListener;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.Rotation;
import imagingbook.pub.geometry.mappings.linear.Translation;


public class ICP_Test implements PlugIn {
	
	static int m = 150;
	static int size = 800;
	static double theta = 0.2;
	static double dx = 0;
	static double dy = 0;
	
	static double sigma = 0;

	
	static double tau = 0.1;
	static int kMax = 20;
	
	AffineMapping A = null;
	List<double[]> X, Y;
	
	Random rnd = new Random(11);
	
	ImagePlus im = null;
	ImageStack stack = null;

	@Override
	public void run(String arg0) {
		IjLogStream.redirectSystem();
		System.out.println("ICP");
		
		A = makeTransformation();
		makeSamplePointsX();
		makeSamplePointsY();
		
		stack = new ImageStack(size, size);
			
		IterativeClosestPointMatcher icp = new IterativeClosestPointMatcher(X, Y, tau, kMax, null, new Listener());
		
		IJ.log("ICP has converged: " + icp.hasConverged());

//		if (!icp.hasConverged())
//			return;
		
//		RealMatrix M = icp.getT();
//		List<double[]> XX = mapPoints(X, M);
//		drawPoints(XX, Color.red);
		
//		int[] Assoc = icp.getA();
//		drawAssociations(X, Y, Assoc, Color.gray);
		
		(new ImagePlus("point associations", stack)).show();
		
	}
	
	private void clearPlot(ImageProcessor ip) {
		//ImageProcessor ip = im.getProcessor();
		ip.setColor(Color.white); 
		ip.fill();
	}

	private void drawPoints(ImageProcessor ip, List<double[]> pnts, Color col) {
		int w = 2;
//		ImageProcessor ip = im.getProcessor();
		ip.setColor(col);
		for (double[] p : pnts) {
			int u = (int) Math.round(p[0]);
			int v = (int) Math.round(p[1]);
			ip.drawLine(u-w, v, u+w, v);
			ip.drawLine(u, v-w, u, v+w);
		}
	}
	
	private void drawAssociations(ImageProcessor ip, List<double[]> pX, List<double[]> pY, int[] Assoc, Color col) {
//		ImageProcessor ip = im.getProcessor();
		ip.setColor(col);
		int i = 0;
		for (double[] px : pX) {
			int j = Assoc[i];
			double[] py = pY.get(j);
			if (i == j) {
				drawLine(ip, px, py);
			}
			else {
				ip.setColor(Color.red);
				drawLine(ip, px, py);
				ip.setColor(col);
			}
			
			i = i + 1;
		}
	}
	
	private void drawLine(ImageProcessor ip, double[] a, double[] b) {
		ip.drawLine(
			(int) Math.round(a[0]), (int) Math.round(a[1]),
			(int) Math.round(b[0]), (int) Math.round(b[1]));
	}

	private AffineMapping makeTransformation() {
		double ctr = 0.5 * size;
		AffineMapping am = new AffineMapping();
		am.concatDestructive(new Translation(-ctr, -ctr));	// TODO: rename to concatD
		am.concatDestructive(new Rotation(theta));
		am.concatDestructive(new Translation(ctr, ctr));
		am.concatDestructive(new Translation(dx, dy));
		return am;
	}

	private void  makeSamplePointsX() {
		X = new ArrayList<double[]>(m);
		for (int i = 0; i < m; i++) {
			double x = rnd.nextInt(size);
			double y = rnd.nextInt(size);
			X.add(new double[] {x, y});
		}
	}

	private void makeSamplePointsY() {
		Y = new ArrayList<double[]>(m);
		for (double[] xi : X) {
			double[] xiT = A.applyTo(xi);
			xiT[0] = xiT[0] + sigma * rnd.nextGaussian();
			xiT[1] = xiT[1] + sigma * rnd.nextGaussian();
			Y.add(xiT);
		}
	}
	
	private List<double[]> mapPoints(RealMatrix M, List<double[]> X) {
		List<double[]> XX = new ArrayList<double[]>(m);
		for (double[] xi : X) {
			double[] xxi = M.operate(Matrix.toHomogeneous(xi));
			XX.add(xxi);
		}
		return XX;
	}
	
	
	/**
	 * An instance of this class may be passed to the constructor
	 * of {@link IterativeClosestPointMatcher}; its {@code notify()} method
	 * is invoked after every iteration of the matcher for debugging
	 * or visualizing intermediate results.
	 */
	private class Listener implements IterationListener {

		@Override
		public void notify(IterativeClosestPointMatcher matcher) {
			ImageProcessor ip = new ColorProcessor(size, size);
			//im = new ImagePlus(this.getClass().getSimpleName(), ip);
			
//			clearPlot(ip);
//			drawPoints(X, Color.blue);
//			drawPoints(Y, Color.green);
			//im.show();
			
			IJ.log("Iteration " + matcher.getIteration() + ": converged = " + matcher.hasConverged());
			RealMatrix T = matcher.getT();
			List<double[]> TX = mapPoints(T, X);
			
			int[] Assoc = matcher.getA();
			
			clearPlot(ip);
			drawPoints(ip, TX, Color.blue);
			drawPoints(ip, Y, Color.green);
			
			drawAssociations(ip, TX, Y, Assoc, Color.gray);
			//im.updateAndDraw();
			stack.addSlice(ip);
			//IJ.wait(1000);
		}
		
	}


}

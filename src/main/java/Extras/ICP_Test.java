package Extras;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.RealMatrix;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.linear.AffineMapping;
import imagingbook.pub.geometry.mappings.linear.Rotation;
import imagingbook.pub.geometry.mappings.linear.Translation;
import imagingbook.pub.geometry.points.IterativeClosestPointMatcher;

public class ICP_Test implements PlugIn {
	
	static int m = 150;
	static int size = 200;
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

	@Override
	public void run(String arg0) {
		IjLogStream.redirectSystem();
		System.out.println("ICP");
		
		A = makeTransformation();
		makeSamplePointsX();
		makeSamplePointsY();
		
		ImageProcessor ip = new ColorProcessor(size, size);
		ip.setColor(Color.white); ip.fill();
		im = new ImagePlus(this.getClass().getSimpleName(), ip);
		drawPoints(X, Color.blue);
		drawPoints(Y, Color.green);
		
		
		IterativeClosestPointMatcher icp = 
				new IterativeClosestPointMatcher(X, Y, tau, kMax);
	
		IJ.log("ICP has converged: " + icp.hasConverged());

//		if (!icp.hasConverged())
//			return;
		
//		RealMatrix M = icp.getT();
//		List<double[]> XX = mapPoints(X, M);
//		drawPoints(XX, Color.red);
		
		int[] Assoc = icp.getA();
		drawAssociations(X, Y, Assoc, Color.gray);
		
		im.show();
		
	}
	


	private void drawPoints(List<double[]> pnts, Color col) {
		ImageProcessor ip = im.getProcessor();
		ip.setColor(col);
		for (double[] p : pnts) {
			int u = (int) Math.round(p[0]);
			int v = (int) Math.round(p[1]);
			ip.drawLine(u-1, v, u+1, v);
			ip.drawLine(u, v-1, u, v+1);
		}
	}
	
	private void drawAssociations(List<double[]> pX, List<double[]> pY, int[] Assoc, Color col) {
		ImageProcessor ip = im.getProcessor();
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
	
	private List<double[]> mapPoints(List<double[]> x2, RealMatrix M) {
		List<double[]> XX = new ArrayList<double[]>(m);
		for (double[] xi : X) {
			double[] xxi = M.operate(Matrix.toHomogeneous(xi));
			XX.add(xxi);
		}
		return XX;
	}
	


}

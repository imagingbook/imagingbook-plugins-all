package Corner_Detection;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.lib.image.ImageGraphics;

public class Make_Corner_Test_Image2 implements PlugIn {
	
	static int width = 512;
	static int height = 512;
	
	static int Nx = 5;
	static int Ny = 5;


	@Override
	public void run(String arg) {

		ByteProcessor ip = new ByteProcessor(width, height);
		ip.setColor(128);
		ip.fill();
		
		
		AffineTransform T = AffineTransform.getTranslateInstance(2.3, 0);
		
		int N = Nx * Ny;
		
		double dx = (double) width / (Nx + 1);
		double dy = (double) height / (Ny + 1);
		
		double rectW = 30;
		double rectH = 50;
		
		double deltaAngle = Math.PI / 2 / N;
		
		try (ImageGraphics g = new ImageGraphics(ip)) {
			Graphics2D g2 = g.getGraphics();
			g.setColor(255);
			
			int k = 0;
			for (int j = 0; j < Ny; j++) {
				double y = dy * (j + 1);
				for (int i = 0; i < Nx; i++) {
					double x = dx * (i + 1);
					Shape rect = new Rectangle2D.Double(x - rectW/2, y - rectH/2, rectW, rectH);
					double angle = deltaAngle * k;
					AffineTransform R = AffineTransform.getRotateInstance(angle, x, y);
					Shape rectR = R.createTransformedShape(rect);
					g2.fill(rectR);
					k++;
				}

			}
			
			//Shape s = makeTriangle(10, 15, 30, 40, 15, 50);
//			Shape rect = new Rectangle2D.Double(50, 300, 20, 15);
//			for (int i=0; i<2*N; i++) {
//				double angle = deltaAngle * i;
//				AffineTransform R = AffineTransform.getRotateInstance(angle, 0.5*width, 0.5*height);
//				Shape s = R.createTransformedShape(rect);
//				g2.fill(s);
//				rect = T.createTransformedShape(rect);
//			}
		}
		
		(new ImagePlus("Corner Test", ip)).show();
	}
	
	Shape makeTriangle(double x0, double y0, double x1, double y1, double x2, double y2) {
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x0, y0);
		poly.lineTo(x1, y1);
		poly.lineTo(x2, y2);
		poly.closePath();
		return poly;
	}

}

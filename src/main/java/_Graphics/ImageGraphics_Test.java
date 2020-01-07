package _Graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ij.ImagePlus;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageGraphics;

/**
 * TODO: add documentation and demo!
 * @author w. Burger
 * @version 2020-01-07
 *
 */
public class ImageGraphics_Test implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_16 + DOES_RGB;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		try (ImageGraphics ig = new ImageGraphics(ip)) {
			Graphics2D g = ig.getGraphics();
			
			g.setColor(Color.white);
			g.drawLine(10, 20, 300, 433);
			g.drawString("Abra", 200, 200);
			
			ig.drawLine(40, 100.5, 250, 101.5);
			
			ig.setAntialiasing(false);
			g.drawLine(10, 400, 300, 20);
			g.drawString("Bubu", 200, 300);
			ig.drawLine(40, 200.5, 250, 201.5);
			
			Point2D p1 = new Point2D.Double(37,66);
			Point2D p2 = new Point2D.Double(90,45);
			Point2D p3 = new Point2D.Double(34,180);
			
			ig.setColor(200);
			ig.setAntialiasing(true);
			ig.setLineWidth(7.5);
			ig.setLineJoinRound();
			ig.drawPolygon(p1, p2, p3);
		}
	}

}

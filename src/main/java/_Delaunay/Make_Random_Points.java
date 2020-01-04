package _Delaunay;

import java.util.Random;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;

public class Make_Random_Points implements PlugIn {
	
	static int size = 400;
	static int NumberOfPoints = 1000;

	@Override
	public void run(String arg) {
		ByteProcessor ip = new ByteProcessor(size, size);
		Random rg = new Random();
		
		for (int i = 0; i < NumberOfPoints; i++) {
			int x = rg.nextInt(size);
			int y = rg.nextInt(size);
			ip.set(x, y, 255);
		}
		
		(new ImagePlus("Random", ip)).show();
	}

}

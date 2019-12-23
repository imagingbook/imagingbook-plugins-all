package Spectral_2.dft;

import java.util.Arrays;

/**
 * TODO: size checking throughout!
 * @author WB
 *
 */
public abstract class Dft2D {
	
	final ScalingMode sm;
	
	private Dft2D() {
		this(ScalingMode.Default);
	}
	
	private Dft2D(ScalingMode sm) {
		this.sm = sm;
	}
	
	// -----------------------------------------------------------------------
	
	public static class Float extends Dft2D implements DiscreteFourierTransform2D.Float {
		
		@Override
		public void forward(float[][] gRe, float[][] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(float[][] GRe, float[][] GIm) {
			transform(GRe, GIm, false);
		}
		
		/**
		 * Transforms the given 2D arrays 'in-place', i.e., real and imaginary
		 * arrays of identical size must be supplied, neither may be null.
		 * 
		 * @param gRe
		 * @param gIm
		 * @param forward
		 */
		void transform(float[][] gRe, float[][] gIm, boolean forward) {
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final float[] rowRe = new float[width];
			final float[] rowIm = new float[width];
			Dft1D.Float dftRow = new Dft1D.Float(width, sm);
			for (int v = 0; v < height; v++) {
				extractRow(gRe, v, rowRe);
				extractRow(gIm, v, rowIm);
				dftRow.transform(rowRe, rowIm, forward);
				insertRow(gRe, v, dftRow.getRe(rowRe));
				insertRow(gIm, v, dftRow.getIm(rowIm));
			}

			// transform each column (in place):
			final float[] colRe = new float[height];
			final float[] colIm = new float[height];
			Dft1D.Float dftCol = new Dft1D.Float(height, sm);
			for (int u = 0; u < width; u++) {
				extractCol(gRe, u, colRe);
				extractCol(gIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(gRe, u, dftCol.getRe(colRe));
				insertCol(gIm, u, dftCol.getIm(colIm));
			}
		}
		

		// extract the values of row 'v' of 'g' into 'row'
		private void extractRow(float[][] g, int v, float[] row) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		// insert 'row' into row 'v' of 'g'
		private void insertRow(float[][] g, int v, float[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// extract the values of column 'u' of 'g' into 'cols'
		private void extractCol(float[][] g, int u, float[] col) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		// insert 'col' into column 'u' of 'g'
		private void insertCol(float[][] g, final int u, float[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
	}
	
	// -----------------------------------------------------------------------
	
	public static class Double extends Dft2D implements DiscreteFourierTransform2D.Double {
		
		@Override
		public void forward(double[][] gRe, double[][] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(double[][] GRe, double[][] GIm) {
			transform(GRe, GIm, false);
		}
		
		/**
		 * Transforms the given 2D arrays 'in-place', i.e., real and imaginary
		 * arrays of identical size must be supplied, neither may be null.
		 * 
		 * @param gRe
		 * @param gIm
		 * @param forward
		 */
		void transform(double[][] gRe, double[][] gIm, boolean forward) {
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final double[] rowRe = new double[width];
			final double[] rowIm = new double[width];
			Dft1D.Double dftRow = new Dft1D.Double(width, sm);
			for (int v = 0; v < height; v++) {
				extractRow(gRe, v, rowRe);
				extractRow(gIm, v, rowIm);
				dftRow.transform(rowRe, rowIm, forward);
				insertRow(gRe, v, dftRow.getRe(rowRe));
				insertRow(gIm, v, dftRow.getIm(rowIm));
			}

			// transform each column (in place):
			final double[] colRe = new double[height];
			final double[] colIm = new double[height];
			Dft1D.Double dftCol = new Dft1D.Double(height, sm);
			for (int u = 0; u < width; u++) {
				extractCol(gRe, u, colRe);
				extractCol(gIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(gRe, u, dftCol.getRe(colRe));
				insertCol(gIm, u, dftCol.getIm(colIm));
			}
		}
		

		// extract the values of row 'v' of 'g' into 'row'
		private void extractRow(double[][] g, int v, double[] row) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		// insert 'row' into row 'v' of 'g'
		private void insertRow(double[][] g, int v, double[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// extract the values of column 'u' of 'g' into 'cols'
		private void extractCol(double[][] g, int u, double[] col) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		// insert 'col' into column 'u' of 'g'
		private void insertCol(double[][] g, final int u, double[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
	}

}

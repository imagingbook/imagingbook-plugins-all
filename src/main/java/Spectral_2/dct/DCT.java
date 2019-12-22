package Spectral_2.dct;

import ij.IJ;

/**
 * 1D and 2D Discrete Cosine transforms (DCT). This version uses tabulated
 * cosine values and is ~10 times faster than using trigonometric function
 * calls.
 * 
 * Based on DMT2-Version 2019!!
 * 
 * @author W. Burger
 * @version 2019-12-23
 */
public abstract class DCT {

	public static boolean beVerbose = false;

	// static methods
	
	/**
	 * Forward DCT on the 1D signal g.
	 * @param g 1D signal to be transformed
	 * @return the resulting DCT spectrum
	 */
	public static float[] fDCT(float[] g) {
		Dct1d dct = new Dct1d(g.length);
		return dct.DCT(g);
	}

	/**
	 * Inverse DCT on the 1D spectrum G.
	 * @param G 1D DCT spectrum to be transformed
	 * @return the reconstructed signal
	 */
	public static float[] iDCT(float[] G) {
		Dct1d dct = new Dct1d(G.length);
		return dct.iDCT(G);
	}
	
	// 2D methods work "in-place"

	public static void fDCT(float[][] g) {
		Dct2d dct = new Dct2d();
		dct.doDct(g, true);
	}

	public static void iDCT(float[][] G) {
		Dct2d dct = new Dct2d();
		dct.doDct(G, false);
	}


	// 1D Discrete Cosine Transform class --------------------------------

	private static class Dct1d {

		private final float c0 = (float) (1 / Math.sqrt(2));
		private final int M;
		private final int M4;
		private final float[] buffer;
		private final float[] cosTab;

		protected Dct1d(int M) { // Constructor
			this.M = M;
			this.M4 = 4 * M;
			this.buffer = new float[M];
			this.cosTab = makeCosTable();
		}

		private float[] DCT(float[] g) {
			if (g.length != M) {
				throw new IllegalArgumentException("g must be of length " + M);
			}
			final double s = (float) Math.sqrt(2.0 / M); // common scale factor
			final float[] G = buffer;
			for (int m = 0; m < M; m++) {
				float cm = (m == 0) ? c0 : 1;
				double sum = 0;
				for (int u = 0; u < M; u++) {
					// double Phi = (Math.PI * m * (2 * u + 1)) / (2 * M);
					// sum = sum + g[u] * cm * Math.cos(Phi);
					int k = (m * (2 * u + 1)) % M4;
					sum = sum + (g[u] * cm * cosTab[k]);
				}
				G[m] = (float) (s * sum);
			}
			return G;
		}

		private float[] iDCT(float[] G) {
			if (G.length != M) {
				throw new IllegalArgumentException("G must be of length " + M);
			}
			final double s = (float) Math.sqrt(2.0 / M); // common scale factor
			final float[] g = buffer;
			for (int u = 0; u < M; u++) {
				double sum = 0;
				for (int m = 0; m < M; m++) {
					float cm = (m == 0) ? c0 : 1;
					// double Phi = (Math.PI * m * (2 * u + 1)) / (2 * M);
					// sum = sum + G[m] * cm * Math.cos(Phi);
					int k = (m * (2 * u + 1)) % M4;
					sum = sum + (G[m] * cm * cosTab[k]);
				}
				g[u] = (float) (s * sum);
			}
			return g;
		}

		private float[] makeCosTable() {
			float[] cosTab = new float[M4];
			for (int i = 0; i < M4; i++) {
				cosTab[i] = (float) Math.cos(2 * i * Math.PI / M4);
			}
			return cosTab;
		}
	}
	
	// 2D Discrete Cosine Transform class --------------------------------

	private static class Dct2d {
		int M, N;
		boolean forward = true;
		float[][] data = null;

		protected Dct2d() {
			// nothing to initialize
		}

		private void doDct(float[][] data, boolean forward) {
			this.data = data;
			this.M = data.length;
			this.N = data[0].length;
			this.forward = forward;
			doRows();
			doColumns();
		}

		private void doRows() {
			// transform each row:
			float[] rowBuf = new float[M];
			Dct1d dftR = new Dct1d(M);
			for (int v = 0; v < N; v++) {
				IJ.showProgress(v, N);
				getRow(v, rowBuf);
				float[] rowDct = forward ? dftR.DCT(rowBuf) : dftR.iDCT(rowBuf);
				putRow(v, rowDct);
			}
		}

		private void doColumns() {
			// transform each column:
			float[] colBuf = new float[N];
			Dct1d dftC = new Dct1d(N);
			for (int u = 0; u < M; u++) {
				IJ.showProgress(u, M);
				getCol(u, colBuf);
				float[] colDft = forward ? dftC.DCT(colBuf) : dftC.iDCT(colBuf);
				putCol(u, colDft);
			}
		}

		private void getRow(int v, float[] row) {
			for (int u = 0; u < M; u++) {
				row[u] = data[u][v];
			}
		}

		private void putRow(int v, float[] row) {
			for (int u = 0; u < M; u++) {
				data[u][v] = row[u];
			}
		}

		private void getCol(int u, float[] column) {
			for (int v = 0; v < N; v++) {
				column[v] = data[u][v];
			}
		}

		private void putCol(int u, float[] column) {
			for (int v = 0; v < N; v++) {
				data[u][v] = column[v];
			}
		}
	}

}

package Color_Quantization.lib.bdsup2sub;

/* * Copyright 2012 Jerry Huxtable / Volker Oth (0xdeadbeef) / Miklos Juhasz (mjuhasz) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
// http://www.javased.com/index.php?source_dir=BDSup2Sub/src/main/java/bdsup2sub/tools/QuantizeFilter.java
//package bdsup2sub.tools; 
 
import java.util.HashMap; 
import java.util.Set; 
 
/** * A filter which quantizes an image to a set number of colors - useful for producing 
 * images which are to be encoded using an index color model. The filter can perform 
 * Floyd-Steinberg error-diffusion dithering if required. At present, the quantization 
 * is done using an octtree algorithm but I eventually hope to add more quantization 
 * methods such as median cut. Note: at present, the filter produces an image which 
 * uses the RGB color model (because the application it was written for required it). 
 * I hope to extend it to produce an IndexColorModel by request. 
 */ 
public class QuantizeFilter  { 
 
    /**     * Floyd-Steinberg dithering matrix. 
     */ 
    private static final int[] FS_MATRIX = { 
         0, 0, 0, 
         0, 0, 7, 
         3, 5, 1, 
    }; 
    private static final int SUM = 3 + 5 + 7 + 1; 
 
    private boolean dither; 
    private int numColors = 255; 
    private boolean serpentine = true; 
 
    /**     * Set the number of colors to quantize to. 
     * @param numColors Number of colors. The default is 256. 
     */ 
    public void setNumColors(int numColors) { 
        this.numColors = Math.min(Math.max(numColors, 8), 256); 
    } 
 
    /**     * Clamp a value to the range 0..255 
     * @param c Value to clamp 
     * @return Clamped value 
     */ 
    private static int clamp(final int c) { 
        if (c < 0) { 
            return 0; 
        } 
        if (c > 255) { 
            return 255; 
        } 
        return c; 
    } 
 
    /**     * Get the number of colors to quantize to. 
     * @return Number of colors. 
     */ 
    public int getNumColors() { 
        return numColors; 
    } 
 
    /**     * Set whether to use dithering or not. If not, the image is posterized. 
     * @param dither True to use dithering 
     */ 
    public void setDither(boolean dither) { 
        this.dither = dither; 
    } 
 
    /**     * Return the dithering setting 
     * @return Current dithering setting 
     */ 
    public boolean getDither() { 
        return dither; 
    } 
 
    /**     * Set whether to use a serpentine pattern for return or not. This can reduce 'avalanche' artifacts in the output. 
     * @param serpentine True to use serpentine pattern 
     */ 
    public void setSerpentine(boolean serpentine) { 
        this.serpentine = serpentine; 
    } 
 
    /**     * Return the serpentine setting 
     * @return Current serpentine setting 
     */ 
    public boolean getSerpentine() { 
        return serpentine; 
    } 
 
    /**     * Quantize picture 
     * @param inPixels Array of RGBA pixels to quantize 
     * @param outPixels Array with quantized palette entries 
     * @param width Width of image 
     * @param height Height of image 
     * @param numColors Number of colors used 
     * @param dither Use dithering? 
     * @param serpentine Use serpentine for dithering? 
     * @return Integer array containing palette information 
     */ 
    public int[] quantize(int[] inPixels, byte[] outPixels, int width, int height, int numColors, boolean dither, boolean serpentine) { 
        int count = width * height; 
        OctTreeQuantizer quantizer = new OctTreeQuantizer(); 
        quantizer.setup(numColors); 
        quantizer.addPixels(inPixels, 0, count); 
        int[] table =  quantizer.buildColorTable(); 
 
        if (dither) { 
            int index; 
            for (int y = 0; y < height; y++) { 
                boolean reverse = serpentine && (y & 1) == 1; 
                int direction; 
                if (reverse) { 
                    index = y * width + width - 1; 
                    direction = -1; 
                } else { 
                    index = y * width; 
                    direction = 1; 
                } 
                for (int x = 0; x < width; x++) { 
                    int rgb1 = inPixels[index]; 
                    int idx = quantizer.getIndexForColor(rgb1); 
                    int rgb2 = table[idx]; 
 
                    outPixels[index] = (byte)(idx&0xff); 
 
                    int a1 = (rgb1 >> 24) & 0xff; 
                    int r1 = (rgb1 >> 16) & 0xff; 
                    int g1 = (rgb1 >> 8) & 0xff; 
                    int b1 = rgb1 & 0xff; 
 
                    int a2 = (rgb2 >> 24) & 0xff; 
                    int r2 = (rgb2 >> 16) & 0xff; 
                    int g2 = (rgb2 >> 8) & 0xff; 
                    int b2 = rgb2 & 0xff; 
 
                    int ea = a1-a2; 
                    int er = r1-r2; 
                    int eg = g1-g2; 
                    int eb = b1-b2; 
 
                    for (int i = -1; i <= 1; i++) { 
                        int iy = i+y; 
                        if (0 <= iy && iy < height) { 
                            for (int j = -1; j <= 1; j++) { 
                                int jx = j+x; 
                                if (0 <= jx && jx < width) { 
                                    int w; 
                                    if (reverse) 
                                        w = FS_MATRIX[(i+1)*3-j+1]; 
                                    else 
                                        w = FS_MATRIX[(i+1)*3+j+1]; 
                                    if (w != 0) { 
                                        int k = reverse ? index - j : index + j; 
                                        rgb1 = inPixels[k]; 
 
                                        a1 = (rgb1 >> 24) & 0xff; 
                                        r1 = (rgb1 >> 16) & 0xff; 
                                        g1 = (rgb1 >> 8) & 0xff; 
                                        b1 = rgb1 & 0xff; 
                                        a1 += ea * w/SUM; 
                                        r1 += er * w/SUM; 
                                        g1 += eg * w/SUM; 
                                        b1 += eb * w/SUM; 
                                        inPixels[k] = (clamp(a1) << 24 | clamp(r1) << 16) | (clamp(g1) << 8) | clamp(b1); 
                                    } 
                                } 
                            } 
                        } 
                    } 
                    index += direction; 
                } 
            } 
        } 
 
        // create palette 
        HashMap<Integer,Integer> p = new HashMap<Integer,Integer>(); 
 
        for (int i = 0; i < count; i++) { 
            int color; 
            if (dither) { 
                color = table[outPixels[i]&0xff]; 
            } else { 
                color = table[quantizer.getIndexForColor(inPixels[i])]; 
            } 
            int idx = p.size(); 
            Integer idxEx = p.get(color); 
            if (idxEx == null) { 
                p.put(color, idx); 
            } else { 
                idx = idxEx; 
            } 
            outPixels[i] = (byte)(idx); 
        } 
 
        Set <Integer>keys = p.keySet(); 
        int pal[] = new int[p.size()]; 
        for (int k : keys) { 
            pal[p.get(k)] = k; 
        } 
 
        return pal; 
    } 
}

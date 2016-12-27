package Color_Quantization.lib.gridinskiy;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/*
 * This Program performs 8 - bit octree quantization on the supplied image
 * https://bitbucket.org/grimm/work/MultiFinal/src/quantize.java 
 * @author Nick Gridinskiy
 */
public class QuantizerGridinskiy {
	
	public BufferedImage out;

    public QuantizerGridinskiy(BufferedImage img, int colors) {
        //lets first start by building a tree.
        octree root = new octree(colors);
        int[][] octo = new int[img.getWidth()][img.getHeight()]; //img represented using the colors in the pallette.
        System.out.println("-- Building the octree(May take a LOOOONG time) ....");

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                root.insert(octo[i][j] = img.getRGB(i, j));
            }
        }

        System.out.println("-- Building image pallette ... " + root.colorCount());

        int[] pallette = new int[colors];

        //temporary variables
        int c = 0; //how many colors are currently in the pallette.
        octree temp;
        int red, green, blue;


        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                temp = root.get(octo[i][j]);


                if (temp.index > -1) {
                    octo[i][j] = temp.index;
                } else {
                    try {
                        red = temp.red / temp.reference;
                        green = temp.green / temp.reference;
                        blue = temp.blue / temp.reference;
                        pallette[c] = ((red << 16) | (green << 8) | blue);
                    } catch (Exception e) {
                        System.out.printf("Oops we were dividing with the reference of %d and index of , %d the RED %d, GREEN %d, BLUE %d, and c is %d\n", temp.reference, temp.index, temp.red, temp.green, temp.blue, c);
                        //System.out.println(e);
                    }


                    octo[i][j] = c;
                    temp.index = c;

                    c++; //lol i always love this.
                }
            }
        }
        
        System.out.println("-- Done building pallete.");
        System.out.println("-- Generating the quantized picture ....");
       out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getWidth(); i++){
            for(int j = 0; j < img.getHeight(); j ++){
                out.setRGB(i, j, pallette[octo[i][j]]);
            }
        }
//        try {
//            javax.imageio.ImageIO.write((RenderedImage)out, "jpg", new File("quantized.jpg"));
//        } catch (IOException ex) {
//            //Logger.getLogger(quantize.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("-- Error Saving output.");
//        }
    }
    
    

    /**
     * This class doubles down as an octree data structure as well as a pixel data structure.
     */
    private class octree {

        octree[] children = new octree[8];
        int red;
        int green;
        int blue;
        int index = -1; //index of this pixel inside of the pallette.
        int max_colors;
        int reference = 0; //Number of references.
        boolean reduced = false; //whether this node has been part of the reduction process.

        public octree() {
        }

        /**
         * Main constructor for the end user.
         * Creates a tree constrained to the number of colors provided.
         * @param max_colors maximum amount of colors the tree can have.
         */
        public octree(int max_colors) {
            this.max_colors = max_colors;
        }

        public octree(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.reference++;
        }


        /**
         * Get a quantized pixel.
         * @param pixel
         * @return 
         */
        public octree get(int pixel) {
            int red = (pixel >>> 16) & 0xFF;
            int green = (pixel >>> 8) & 0xFF;
            int blue = (pixel) & 0xFF;
            octree r = get(red, green, blue, 0);

            return r;
        }

        octree get(int red, int green, int blue, int level) {
            int ind = ((red >>> level & 1) << 2) | ((green >>> level & 1) << 1) | (blue >>> level & 1);
            try {
                if (children[ind] == null) {
                    if (this.reference == 0) {
                        System.out.printf("OOPsie we are not supposed to be returning 0 referenced pixel wtf %d, %d \n", level, ind);
                        System.exit(1);
                    }
                    return this;
                } else {
                    return children[ind].get(red, green, blue, level + 1);
                }
            } catch (Exception e) {
                System.out.printf("Crashed with index of %d and level of %d \n", ind, level);
                System.out.println(e);
            }
            return null;
        }

        /**
         * Insert a color.
         * This method is meant to be used by user.
         * @param red
         * @param green
         * @param blue
         * @return true- if we made a new leaf false - if we didnt.
         */
        public boolean insert(int pixel) {

            int red = (pixel >>> 16) & 0xFF;
            int green = (pixel >>> 8) & 0xFF;
            int blue = (pixel) & 0xFF;
            insert(red, green, blue, 0);
            int temp;
            octree r;
            while((temp = this.colorCount()) > max_colors){
                r = this.getRedactible();
                r.reduce();
                //System.out.println(r.reference + ", " + temp + ", " + max_colors  + ", " + r.red + ", " + r.green + ", " + r.blue);
            }
            return false;
        }

        /**
         * Inserts a color.
         * Internal method.
         * @param red
         * @param green
         * @param blue
         * @param level current level of recursion must be below 8, since each channel is 8 bits.
         * @return whether or not we created a new leaf
         */
        public boolean insert(int red, int green, int blue, int level) {
            int ind = (((red >>> level) & 1) << 2) | (((green >>> level) & 1) << 1) | ((blue >>> level) & 1);

            // if this is a leaf node then we do the following base cases.
            if (this.reference != 0) {
                this.reference++;
                this.red += red;
                this.green += green;
                this.blue += blue;
                return false;
            } else if (level == 7 && children[ind] == null) {
                children[ind] = new octree(red, green, blue);
                return true;
            }
            
            //at this point we know that we arent in the leaf, so lets get to one.
            if (children[ind] == null) {
                children[ind] = new octree();
            }

            return children[ind].insert(red, green, blue, level + 1);
        }

        /**
         * Get the best node that can be reduced.
         * @return 
         */
        octree getRedactible() {
            octree[] candidates = new octree[8];
            octree best = null;
            //best.reference = Integer.MAX_VALUE;
            int numCandidates = 0;
            int temp1 = Integer.MAX_VALUE, temp2;
            
            if(this.getReferences() > 0)
                return this;
            
            for (int i = 0; i < 8; i ++){
                if(children[i] == null)
                    continue;
                candidates[numCandidates] = children[i].getRedactible();
                if(candidates[numCandidates] != null)
                    numCandidates ++;
            }
            
            for(int i = 0; i < numCandidates; i++){
                temp2 = candidates[i].getReferences();
                if(temp1 > temp2 && temp2 != 0){
                    best = candidates[i];
                    temp1 = best.getReferences();
                    //System.out.println("The best is " + best.getReferences());
                }
            }
            //if (best == null)
                //System.out.println("OOpsie we got a problem");
           return best;
        }

        /**
         * Reduces this node into the parent.
         * @return how many pixels got eliminated.
         */
        int reduce() {
            int count = 0;
            for(int i = 0; i < 8; i ++){
                if(children[i] == null )
                    continue;
                this.red += children[i].red;
                this.green += children[i].green;
                this.blue += children[i].blue;
                this.reference += children[i].reference;
                count ++;
                children[i] = null;
            }

            this.reduced = true;
            return count;
        }

        /**
         * Count the actual amount of colors stored in the tree.
         * AKA Leaf nodes.
         * @return 
         */
        int colorCount() {
            int count = 0;
            for (int i = 0; i < 8; i++) {
                if (children[i] == null) {
                    continue;
                }
                count += children[i].colorCount();
            }
            if (count == 0) {
                count++;
            }
            return count;
        }
        
        /**
         * Gets the amount of pixel references are in this node.
         * This is necessary for the reduction algorithm.
         * @return 
         */
        int getReferences(){
            int refs = 0;
            
            Boolean temp = null;
            if(this.reduced)
                return 0;
            
            
            for(int i = 0; i < 8; i ++){
                if(children [i] == null)
                    continue;
                if(temp == null)
                    temp = children[i].reduced;
                if(children[i].reduced != temp)
                    return 0;
                refs += children[i].reference;
            }
            return refs;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }
        try {
            new QuantizerGridinskiy(javax.imageio.ImageIO.read(new File(args[0])), Integer.parseInt(args[1]));
            System.out.println("* FINISHED*");
        } catch (IOException ex) {
            //Logger.getLogger(quantize.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-- Error loading the file provided.");
        }
    }
}

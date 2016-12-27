package Color_Quantization.lib.bdsup2sub;

import java.util.Vector;

import Color_Quantization.lib.bdsup2sub.OctTreeQuantizer.OctTreeNode;

/** * An image Quantizer based on the Octree algorithm. This is a very basic implementation 
 * at present and could be much improved by picking the nodes to reduce more carefully 
 * (i.e. not completely at random). 
 */ 
public class OctTreeQuantizer { 
 
    /** The greatest depth the tree is allowed to reach */ 
    private static final int MAX_LEVEL = 5; 
 
    /**     * An Octtree node. 
     */ 
    static class OctTreeNode { 
        int children; 
        int level; 
        OctTreeNode parent; 
        OctTreeNode leaf[] = new OctTreeNode[16]; 
        boolean isLeaf; 
        int count; 
        int totalAlpha; 
        int totalRed; 
        int totalGreen; 
        int totalBlue; 
        int index; 
    } 
 
    private OctTreeNode root; 
    private int reduceColors; 
    private int maximumColors; 
    private int colors = 0; 
    @SuppressWarnings("rawtypes") 
    private Vector[] colorList; 
 
    @SuppressWarnings("rawtypes") 
    public OctTreeQuantizer() { 
        setup(256); 
        colorList = new Vector[MAX_LEVEL+1]; 
        for (int i = 0; i < MAX_LEVEL+1; i++) 
            colorList[i] = new Vector(); 
        root = new OctTreeNode(); 
    } 
 
    /**     * Initialize the quantizer. This should be called before adding any pixels. 
     * @param numColors Number of colors we're quantizing to. 
     */ 
    public void setup(int numColors) { 
        maximumColors = numColors; 
        reduceColors = Math.max(512, numColors * 2); 
    } 
 
    /**     * Add pixels to the quantizer. 
     * @param pixels Array of ARGB pixels 
     * @param offset Offset into the array 
     * @param count Count of pixels 
     */ 
    public void addPixels(int[] pixels, int offset, int count) { 
        for (int i = 0; i < count; i++) { 
            insertColor(pixels[i+offset]); 
            if (colors > reduceColors) { 
                reduceTree(reduceColors); 
            } 
        } 
    } 
 
    /**     * Get the color table index for a color. 
     * @param argb Color in ARGB format 
     * @return Index of color in table 
     */ 
    public int getIndexForColor(int argb) { 
        int alpha = (argb >> 24) & 0xff; 
        int red   = (argb >> 16) & 0xff; 
        int green = (argb >> 8) & 0xff; 
        int blue  = argb & 0xff; 
 
        OctTreeNode node = root; 
 
        for (int level = 0; level <= MAX_LEVEL; level++) { 
            OctTreeNode child; 
            int bit = 0x80 >> level; 
 
            int index = 0; 
            if ((alpha & bit) != 0) { 
                index += 8; 
            } 
            if ((red & bit) != 0) { 
                index += 4; 
            } 
            if ((green & bit) != 0) { 
                index += 2; 
            } 
            if ((blue & bit) != 0) { 
                index += 1; 
            } 
 
            child = node.leaf[index]; 
 
            if (child == null) { 
                return node.index; 
            } else if (child.isLeaf) { 
                return child.index; 
            } else { 
                node = child; 
            } 
        } 
        return 0; 
    } 
 
    @SuppressWarnings("unchecked") 
    private void insertColor(int rgb) { 
        int alpha = (rgb >> 24) & 0xff; 
        int red = (rgb >> 16) & 0xff; 
        int green = (rgb >> 8) & 0xff; 
        int blue = rgb & 0xff; 
 
        OctTreeNode node = root; 
 
        // System.out.println("insertColor="+Integer.toHexString(rgb)); 
        for (int level = 0; level <= MAX_LEVEL; level++) { 
            OctTreeNode child; 
            int bit = 0x80 >> level; 
 
            int index = 0; 
            if ((alpha & bit) != 0) { 
                index += 8; 
            } 
            if ((red & bit) != 0) { 
                index += 4; 
            } 
            if ((green & bit) != 0) { 
                index += 2; 
            } 
            if ((blue & bit) != 0) { 
                index += 1; 
            } 
 
            child = node.leaf[index]; 
 
            if (child == null) { 
                node.children++; 
 
                child = new OctTreeNode(); 
                child.parent = node; 
                node.leaf[index] = child; 
                node.isLeaf = false; 
                colorList[level].addElement(child); 
 
                if (level == MAX_LEVEL) { 
                    child.isLeaf = true; 
                    child.count = 1; 
                    child.totalAlpha = alpha; 
                    child.totalRed = red; 
                    child.totalGreen = green; 
                    child.totalBlue = blue; 
                    child.level = level; 
                    colors++; 
                    return; 
                } 
 
                node = child; 
            } else if (child.isLeaf) { 
                child.count++; 
                child.totalAlpha += alpha; 
                child.totalRed += red; 
                child.totalGreen += green; 
                child.totalBlue += blue; 
                return; 
            } else 
                node = child; 
        } 
    } 
 
    @SuppressWarnings("rawtypes") 
    private void reduceTree(int numColors) { 
        for (int level = MAX_LEVEL-1; level >= 0; level--) { 
            Vector v = colorList[level]; 
            if (v != null && v.size() > 0) { 
                for (int j = 0; j < v.size(); j++) { 
                    OctTreeNode node = (OctTreeNode)v.elementAt(j); 
                    if (node.children > 0) { 
                        for (int i = 0; i < node.leaf.length; i++) { 
                            OctTreeNode child = node.leaf[i]; 
                            if (child != null) { 
                                node.count += child.count; 
                                node.totalAlpha += child.totalAlpha; 
                                node.totalRed += child.totalRed; 
                                node.totalGreen += child.totalGreen; 
                                node.totalBlue += child.totalBlue; 
                                node.leaf[i] = null; 
                                node.children--; 
                                colors--; 
                                colorList[level+1].removeElement(child); 
                            } 
                        } 
                        node.isLeaf = true; 
                        colors++; 
                        if (colors <= numColors) 
                            return; 
                    } 
                } 
            } 
        } 
    } 
 
    /**     * Build the color table. 
     * @return Color table 
     */ 
    public int[] buildColorTable() { 
        int[] table = new int[colors]; 
        buildColorTable(root, table, 0); 
        return table; 
    } 
 
    /**     * A quick way to use the quantizer. Just create a table the right size and pass in the pixels. 
     * @param inPixels Integer array containing the pixels 
     * @param table Output color table 
     */ 
    public void buildColorTable(int[] inPixels, int[] table) { 
        int count = inPixels.length; 
        maximumColors = table.length; 
        for (int i = 0; i < count; i++) { 
            insertColor(inPixels[i]); 
            if (colors > reduceColors) { 
                reduceTree(reduceColors); 
            } 
        } 
        if (colors > maximumColors) { 
            reduceTree(maximumColors); 
        } 
        buildColorTable(root, table, 0); 
    } 
 
    /**     * Build color table 
     * @param node Octree node 
     * @param table Color table 
     * @param index Index 
     * @return Index 
     */ 
    private int buildColorTable(OctTreeNode node, int[] table, int index) { 
        if (colors > maximumColors) { 
            reduceTree(maximumColors); 
        } 
 
        if (node.isLeaf) { 
            int count = node.count; 
            table[index] = 
                ((node.totalAlpha/count) << 24) | 
                ((node.totalRed/count)   << 16) | 
                ((node.totalGreen/count) <<  8) | 
                node.totalBlue/count; 
            node.index = index++; 
        } else { 
            for (int i = 0; i < node.leaf.length; i++) { 
                if (node.leaf[i] != null) { 
                    node.index = index; 
                    index = buildColorTable(node.leaf[i], table, index); 
                } 
            } 
        } 
        return index; 
    } 
}
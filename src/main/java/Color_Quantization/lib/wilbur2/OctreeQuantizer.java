package Color_Quantization.lib.wilbur2;

import ij.IJ;
//import ij.IJ;
import ij.process.ColorProcessor;

/**
 * The octree quantization method reduces a set of colors in an image to a
 * desired color palette size while maintaining as much image quality as
 * possible. The principle structure employed here is an octree, or tree with a
 * branching factor of 8. The tree is also 8 nodes deep, as each color may be
 * represented by an 8 bit number.
 * 
 * The image is scanned and pixel colors are read into the tree and stored one
 * at a time. Branch selection is as follows: at depth n, the nth most
 * significant r, g, and b values form rgb, a binary number from 0 to 7. The
 * red, green, and blue values of the entire color are added to red, green, and
 * blue fields at every node along the way, so every node has the sum of color
 * value for all of its children. The number of unique colors in the tree is
 * noted, though red, green, and blue value totals per node as described above
 * are done for *each* pixel.
 * 
 * Once the number of unique colors exceeds the desired threshhold, from 2 to 8
 * colors are combined into a single color. A search is done from the bottom of
 * the tree, since colors with a common node share all of that nodes's parental
 * hierarchy. Nodes in deeper levels are combined first, however which node is
 * combined first within a row results in slightly different outcomes, which are
 * not discussed here. In this implementation, the first viable node is reduced.
 * 
 * Once a node has been selected, its children are removed and it takes a color
 * equal to a weighted average of its children. Since color sums are kept in
 * each node, all that needs be done is divide the total red, green, and blue
 * values by the number of pixels passing through the node.
 * 
 * Note that further colors that would have been added to pruned nodes are not
 * added further than the prune. The image color is not changed either, though
 * red, green, and blue totals are still updated as before.
 * 
 * Once the image has been scanned in, it is trivial to map a color to its
 * equivalent from the new color table. Simply traverse the tree as before,
 * stopping when a node with no children is reached and return that node's color
 * value.
 * 
 * This algorithm is made more efficient by the use of color lists to more
 * quickly find nodes to reduce. As nodes are created they are added to the
 * appropriate list for their level. When a reduce is performed, the lists are
 * scanned, one at a time, for a viable node. If the end of a list is reached,
 * the next highest list is scanned, and no further node will ever be created in
 * the lower list. This thus makes the adding nodes operation progressively
 * faster.
 */
public class OctreeQuantizer {

	// and this with a color to preserve transparency
	private static final int TRANSPARENCY_MASK = 0xff000000;
	// and this with a color to preserve rgb
	private static final int RGB_MASK = 0x00ffffff;
	// and this with an int to keep only the lower 8 bits
	private static final int BYTE_MASK = 0xff;
	
	// the size of the desired color table
	private final int maxColors;

	// heads of lists of available nodes. Where all _pickNode calls look from
	private final Node[] listHead;

	// tails of lists of available nodes. Where new nodes are added
	private final Node[] listEnd;

	private final Node root; // root node of the tree
	
	private int colorCnt = 0; // how many colors currently in the tree
	private int maxDepth; // max. relevant depth of the tree

	
	public OctreeQuantizer(ColorProcessor cp, int maxColors) {
		this((int[]) cp.getPixels(), maxColors);
	}

	public OctreeQuantizer(int[] pixels, int maxColors) {
		if (maxColors < 2) {
			throw new IllegalArgumentException("maxColors < 2");
		}
		this.maxColors = maxColors;
		maxDepth = 8;
		this.listHead = new Node[maxDepth];
		this.listEnd = new Node[maxDepth];
		this.root = new Node(null);
		
		for (int rgb : pixels) {
			this.addColor(rgb);
		}
	}

	/**
	 * Add a color to the tree. This is a wrapper for a call to the root
	 * OctreeNode.
	 * 
	 * @param rgb The color to be added
	 */
	public void addColor(int rgb) {
		root.addColor(rgb);
	}

	/**
	 * Map a color to the tree. This is a wrapper for a call to the root
	 * OctreeNode.
	 * 
	 * @param rgb The color to be mapped
	 * @return The mapped color
	 */
	public int mapColor(int rgb) {
		return root.mapColor(rgb);
	}
	
	// -----------------------  non-public methods --------------------------

	private Node getListHead(int i) {
		return listHead[i];
	}

	// define the beginning of a list
	private void setListHead(int i, Node node) {
		listHead[i] = node;
		listEnd[i] = node;
	}

	// add to a list
	private void setListEnd(int i, Node node) {
		listEnd[i].setNext(node); // continue the chain
		listEnd[i] = node;
	}

	// pick the first viable node (lowest level that will remove at least
	// one color
	private Node pickNode() {
		// apparently, once you've risen a level, you don't go back. I
		// question apple's claim, but it works okay nonetheless.
		// why _maxDepth-2? -1 since _maxDepth starts at 8 and the array is
		// 0-based.
		// -2 because we're only concerned with the seventh level and above -
		// leaves
		// can't be pruned.
		for (int i = maxDepth - 2; i >= 0; i--) {
			// traverse list i for a good node
			Node curNode = getListHead(i);
			Node prevNode = null; // previously selected node

			// picking specific node
			Node saveNode = null;
			Node savePrev = null;
			// traverse the entire level looking for the best node, which is
			// placed in saveNode
			while (curNode != null) {
				// usable nodes have siblings
				if (curNode.getChildren() > 1) {
					// picking specific node
					if ((saveNode == null) || (curNode.getChildren() < saveNode.getChildren())) {
						saveNode = curNode;
						savePrev = prevNode;
					}
				}
				// still looking
				prevNode = curNode;
				curNode = curNode.getNext();
			}
			if (saveNode != null) {
				// if we picked the first node, then we have to change the value
				// in the list of heads.
				if (savePrev == null) {
					listHead[i] = saveNode.getNext();
				} else {
					Node nextNode = saveNode.getNext();
					// if we picked the last node, then we have to change the
					// value
					// in the list of ends
					if (nextNode == null) {
						listEnd[i] = savePrev;
					}
					savePrev.setNext(nextNode); // otherwise preserve the chain
					// if nextNode is null, we still want to erase n from p.next
				}
				return saveNode;
			}
			// we've exhausted this level
			maxDepth--;
		}
		return null;
	}

	// get rid of some nodes
	private void reduce() {
		// which node to reduce?
		Node node = pickNode();

		// now no colors will be created under this node
		node.setMaxLevel(node.getLevel());

		// determine color based on weighted average
		node.updateColor();

		colorCnt = colorCnt - (node.getChildren() - 1); // children are pruned

		// now children will never be used by _pickNode
		node.setChildren(0);
	}
	
//	private int[] RGB = new int[3];
//			
//	private void intToRgb(int rgb, int[] RGB) {
//		RGB[0] = (rgb >> 16) & BYTE_MASK;
//		RGB[1] = (rgb >> 8) & BYTE_MASK;
//		RGB[2] = rgb & BYTE_MASK;
//	}
	
	// ---------------------- inner class Node ----------------------------------------------

	private class Node {
		private final Node[] leaf; // 8 children of node
		private final int level; // where does this node lie in the tree? TODO: should be final
		
		private int children; // how many leaves are filled
		private Node nextNode; // next node at this level
		private int maxLevel; // lowest permissible depth at this node

		// sum of the (r/g/b) values of every node beneath this, incremented
		// for every pixel that passes through.
		private long totalR   = 0;
		private long totalG = 0;
		private long totalB  = 0;
		private int pixCnt = 0; // how many pixels have passed through this node

		// color value at this node. If _colorSet is false,
		// this value is meaningless (and a color is to be found further down
		// the tree)
		private int color;
		private boolean colorSet; // is the color set?
		
		// used to create all nodes in the tree other than the root
		private Node(Node p) {
			this.leaf = new Node[8];
			this.level = (p != null) ? p.getLevel() + 1 : 0;
			this.maxLevel = 8;
			this.children = 0;
			this.colorSet = false;
			if (p != null) {
				p.incChildren();
			}
		}


		/**
		 * Put a color into the OctreeQuantizer, reducing as necessary
		 * @param rgb The color, in AARRGGBB format.
		 */
		private void addColor(int rgb) {
			//int index = updateColorStats(rgb, true);
			updateNodeColors(rgb);
			int index = getBranchIndex(rgb);
			// as long as we haven't reached a leaf, we keep on descending
			if (level < maxLevel && level < maxDepth) {
				if (leaf[index] == null) {
					Node newNode = new Node(this);
					leaf[index] = newNode; // the new child
					if (newNode.getLevel() >= maxDepth) {
						colorCnt = colorCnt + 1;
						if (colorCnt > maxColors) {
							reduce();
						}
					}

					// is there a list of nodes at this level?
					if (getListHead(level) == null) {
						// create the list of nodes at this level
						setListHead(level, newNode);
					} else {
						// add to the list of nodes at this level
						setListEnd(level, newNode);
					}
				}
				// recursive
				leaf[index].addColor(rgb);
			} 
			else {
				if (!colorSet)
					updateColor();
			}
		}

		/**
		 * Map one color onto another from a limited palette.
		 * @param rgb The original color
		 * @return The palette-limited color
		 */
		private int mapColor(int rgb) {
			// once the leaf level is hit
			if (colorSet) {
				// keep transparency from old color, only rgb from new color
				return (rgb & TRANSPARENCY_MASK) | (color & RGB_MASK);
			}

			// traversing the tree
			int index = getBranchIndex(rgb); // updateColorStats(rgb, false);

			// if the color isn't in the map, send it back
			if (leaf[index] == null) {
				return rgb;
			}
			else {
				return leaf[index].mapColor(rgb);
			}
		}

		private int getChildren() {
			return children;
		}

		private int getLevel() {
			return level;
		}

		private Node getNext() {
			return nextNode;
		}

		private void setChildren(int i) {
			if (i <= 8) // don't want to be a nontree :-) // TODO: CHECK!!
				children = i;
		}

		// using maxColor values, picks a viable color
		private void updateColor() {
			// update the node's average color
			//color = (int) (((totalR / pixelCount) << 16) + ((totalG / pixelCount) << 8) + (totalB / pixelCount));
			int avgR = Math.round((float) totalR / pixCnt);	
			int avgG = Math.round((float) totalG / pixCnt);
			int avgB = Math.round((float) totalB / pixCnt);
			color = (avgR << 16) + (avgG << 8) + avgB;	// wilbur's version
			colorSet = true;
		}

		private void setMaxLevel(int level) {
			if (level < 1)
				throw new IllegalArgumentException("level < 1");
			// you can only go lower
			if (level < maxLevel)
				maxLevel = level;
		}

		private void setNext(Node node) {
			nextNode = node;
		}

		private void incChildren() {
			children = children + 1;
		}

		// incrementor functions for values that only go up.
//		private int updateColorStats(int rgb, boolean isNew) {
//			int red = (rgb >> 16) & BYTE_MASK;
//			int green = (rgb >> 8) & BYTE_MASK;
//			int blue = rgb & BYTE_MASK;
//			int index = 0;
//
//			if (isNew) { // update the node's color statistics
//				totalR = totalR + red;
//				totalG = totalG + green;
//				totalB = totalB + blue;
//				pixCnt++;
//			}
//			// choose which leaf to follow
//			red   = red   << level;
//			green = green << level;
//			blue  = blue  << level;
//			// 0-7 determines all values of 3-digit binary RGB. TODO: CHECK/FIX
//			if ((byte) red < 0)
//				index += 4;
//			if ((byte) green < 0)
//				index += 2;
//			if ((byte) blue < 0)
//				index += 1;
//			
//			// return the index of the next branch
//			return index;
//		}
		
		// wilbur: new method
		private void updateNodeColors(int rgb) {
			//update the node's color statistics
			totalR = totalR + ((rgb >> 16) & BYTE_MASK);
			totalG = totalG + ((rgb >> 8) & BYTE_MASK);
			totalB = totalB + (rgb & BYTE_MASK);
			pixCnt++;
		}
		
		// wilbur: new method
		private int getBranchIndex(int rgb) {
			int red = (rgb >> 16) & BYTE_MASK;
			int grn = (rgb >> 8) & BYTE_MASK;
			int blu = rgb & BYTE_MASK;
			
			// choose which leaf to follow (combine the level-th bit of the 3 color components)
//			red = red << level;
//			grn = grn << level;
//			blu = blu << level;
			int index = 0;
			// 0-7 determines all values of 3-digit binary RGB. TODO: CHECK/FIX
			if (((red << level) & 0x80) > 0)   // ((byte) red < 0)
				index += 4;
			if (((grn << level) & 0x80) > 0)   // ((byte) grn < 0)
				index += 2;
			if (((blu << level) & 0x80) > 0)   // ((byte) blu < 0)
				index += 1;
			//IJ.log(String.format("r=%d g=%d b=%d, index=%d", red,grn,blu,index));
			// return the index of the next branch (index = 0,...,7)
			return index;
		}

	}
	
	

}

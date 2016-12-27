package Color_Quantization.lib.apache;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//package org.apache.myfaces.trinidadinternal.image.encode;

/**
 * The node elements that make up an OctreeQuantizer.
 * <p>
 * 
 * @version $Name: $ ($Revision:
 *          adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/image/encode/OctreeNode.java#0
 *          $) $Date: 15-nov-2005.17:46:01 $
 * @since 0.1.4
 * @see OctreeQuantizer
 */
class OctreeNode {

	// and this with a color to preserve transparency
	private static final int TRANSPARENCY_MASK = 0xff000000;
	// and this with a color to preserve rgb
	private static final int RGB_MASK = 0x00ffffff;
	// and this with an int to keep only the lower 8 bits
	private static final int BYTE_MASK = 0xff;

	
	private final OctreeQuantizer tree; // the tree from whence this node was spawned
	private final OctreeNode[] leaf; // 8 children of node
	private int level; // where does this node lie in the tree? TODO: should be final
	
	private int children; // how many leaves are filled
	private OctreeNode nextNode; // next node at this level
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

	/**
	 * Create a new node, which will be the root of the octree.
	 */
	public OctreeNode(OctreeQuantizer tree) {
		this.tree = tree;
		this.leaf = new OctreeNode[8];
		this.level = 0;
		this.maxLevel = 8;
		this.children = 0;
		this.colorSet = false;
	}
	
	// used to create all nodes in the tree other than the root
	private OctreeNode(OctreeNode p) {
		this(p.getTree());
		level = p.getLevel() + 1;
		p.incChildren();
	}

	/**
	 * Put a color into the OctreeQuantizer, reducing as necessary
	 * 
	 * @param rgb
	 *            The color, in AARRGGBB format.
	 */
	void addColor(int rgb) {
		int index = setColorVals(rgb, true);
		// as long as we haven't reached a leaf, we keep on descending
		if (getLevel() < maxLevel && getLevel() < tree.getMaxDepth()) {
			if (leaf[index] == null) {
				OctreeNode newNode = new OctreeNode(this);
				leaf[index] = newNode; // the new child
				if (newNode.getLevel() == tree.getMaxDepth()) {
					tree.incColors();
				}

				// is there a list of nodes at this level?
				if (tree.getListHead(getLevel()) == null) {
					// create the list of nodes at this level
					tree.setListHead(getLevel(), newNode);
				} else {
					// add to the list of nodes at this level
					tree.setListEnd(getLevel(), newNode);
				}
			}
			leaf[index].addColor(rgb);
		} else {
			if (!colorSet)
				updateColor();
		}
	}

	/**
	 * Map one color onto another from a limited palette.
	 * @param rgb The original color
	 * @return The palette-limited color
	 */
	public int mapColor(int rgb) {
		// once the leaf level is hit
		if (colorSet) {
			// keep transparency from old color, only rgb from new color
			return (rgb & TRANSPARENCY_MASK) | (color & RGB_MASK);
		}

		// traversing the tree
		int index = setColorVals(rgb, false);

		// if the color isn't in the map, send it back
		if (leaf[index] == null)
			return rgb;

		return leaf[index].mapColor(rgb);
	}

	int getChildren() {
		return children;
	}

	int getLevel() {
		return level;
	}

	OctreeNode getNext() {
		return nextNode;
	}

	void setChildren(int i) {
		if (i <= 8) // don't want to be a nontree :-)
			children = i;
	}

	// using maxColor values, picks a viable color
	void updateColor() {
		// update the node's average color
		//color = (int) (((totalR / pixelCount) << 16) + ((totalG / pixelCount) << 8) + (totalB / pixelCount));
		int avgR = Math.round((float) totalR / pixCnt);	
		int avgG = Math.round((float) totalG / pixCnt);
		int avgB = Math.round((float) totalB / pixCnt);
		color = (avgR << 16) + (avgG << 8) + avgB;	// wilbur's version
		colorSet = true;
	}

	void setMaxLevel(int level) {
		if (level < 1)
			throw new IllegalArgumentException("level < 1");
		// you can only go lower
		if (level < maxLevel)
			maxLevel = level;
	}

	void setNext(OctreeNode node) {
		nextNode = node;
	}

	private OctreeQuantizer getTree() {
		return tree;
	}

	private void incChildren() {
		children = children + 1;
	}

	// incrementor functions for values that only go up.
	private int setColorVals(int rgb, boolean isNew) {
		int red = (rgb >> 16) & BYTE_MASK;
		int green = (rgb >> 8) & BYTE_MASK;
		int blue = rgb & BYTE_MASK;
		int index = 0;

		if (isNew) { // update the node's color statistics
			totalR = totalR + red;
			totalG = totalG + green;
			totalB = totalB + blue;
			pixCnt++;
		}
		// choose which leaf to follow
		int k = getLevel();
		red =   red << k;
		green = green << k;
		blue =  blue << k;
		// 0-7 determines all values of 3-digit binary RGB. TOWO: CHECK/FIX
		if ((byte) red < 0)
			index += 4;
		if ((byte) green < 0)
			index += 2;
		if ((byte) blue < 0)
			index += 1;
		return index;
	}

}

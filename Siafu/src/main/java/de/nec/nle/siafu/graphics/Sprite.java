/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.siafu.graphics;

import org.eclipse.swt.graphics.Image;

/**
 * A sprite is an object that contains the images for an Agent simulation, as
 * he faces each possible direction.
 * 
 * @author Miquel Martin
 * 
 */
public class Sprite {
	/** Maximum number of directions. */
	private static final int MAX_DIR = 8;

	/** The name of the sprite. */
	private String name;

	/**
	 * The vertical offset of the sprite, that is, how much space to leave
	 * from the pivot point to the beginning of the image.
	 */
	private int vOffset;

	/**
	 * The horizontal offset of the sprite, that is, how much space to leave
	 * from the pivot point to the beginning of the image.
	 */
	private int hOffset;

	/** The array with the images for each direction. */
	private Image[] directionImages = new Image[MAX_DIR];

	/**
	 * Create a sprite.
	 * 
	 * @param name the sprite's name
	 * @param vOffset the vertical offset
	 * @param hOffset the horizontal offset
	 */
	public Sprite(final String name, final int vOffset, final int hOffset) {
		this.name = name;
		this.vOffset = vOffset;
		this.hOffset = hOffset;
	}

	/**
	 * Get the sprite's name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the image for a particular direction.
	 * 
	 * @param dirIndex the required direction
	 * @return the Image that represents the agent facing that way
	 */
	public Image getImage(final int dirIndex) {
		return directionImages[dirIndex];
	}

	/**
	 * Get the horizontal offset of the image, that is, the distance between
	 * the left most pixel and the pivot point.
	 * 
	 * @return the offset
	 */
	public int getHOffset() {
		return hOffset;
	}

	/**
	 * Get the vertical offset of the image, that is, the distance between the
	 * top most pixel and the pivot point.
	 * 
	 * @return the offset
	 */
	public int getVOffset() {
		return vOffset;
	}

	/** Set the image for the direction given in dirIndex.
	 * 
	 * @param dirIndex the direction
	 * @param image the image for the direction
	 */
	public void setImage(final int dirIndex, final Image image) {
		directionImages[dirIndex] = image;

	}

	/**
	 * Dispsoe of the SWT allocated resources.
	 *
	 */
	public void disposeResources() {
		for (Image img : directionImages) {
			img.dispose();
		}
	}
}

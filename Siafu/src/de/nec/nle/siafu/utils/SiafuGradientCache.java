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

package de.nec.nle.siafu.utils;

import java.util.Iterator;

import de.nec.nle.siafu.control.Controller;

/**
 * This class is identical to <code>PersitentCachedMaps</code>, except that
 * it prints its messages through a Progress interface.
 * 
 * @author Miquel Martin
 * 
 */
public class SiafuGradientCache extends PersistentCachedMap {

	/**
	 * Creates a PersistentCachedMap for the distance Gradients.
	 * 
	 * @param basePath the directory that contains all of the persisted maps
	 * @param name the name of the map.
	 * @param cacheSize the amount of maps to keep in memory at any time. When
	 *            this number is reached, the next loaded map will force the
	 *            oldest one in memory to be removed, becoming only accessible
	 *            through hard drive read.
	 * @param fillCache whether you would like to read the first cacheSize
	 *            elements from the hard drive and put them in the cache.
	 * @see PersistentCachedMap
	 */
	public SiafuGradientCache(final String basePath, final String name,
			final int cacheSize, final boolean fillCache) {
		super(basePath, name, cacheSize, fillCache);
	}


	/**
	 * Load the first desiredCacheSize persisted elements to memory for faster
	 * access.
	 * 
	 * @param desiredCacheSize the number of elements to read
	 */
	public void fillCache(final int desiredCacheSize) {
		Controller.getProgress().reportCachePrefill(
			Math.min(desiredCacheSize, toc.size()));

		this.cacheSize = desiredCacheSize;

		Iterator<String> tocIt = toc.iterator();

		while (tocIt.hasNext() && (cache.size() < desiredCacheSize)) {
			this.get(tocIt.next());
			Controller.getProgress().reportCacheElementLoaded();
		}

		Controller.getProgress().reportCachePrefillEnded();
	}
}

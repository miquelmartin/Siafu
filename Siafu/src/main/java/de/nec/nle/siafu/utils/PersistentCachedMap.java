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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An implementation of the Map interface which also provides persistance and
 * handling of big amounts of memory. When new object is added, it is
 * persisted into path/mapName, where path defaults to
 * $user.home/.PersistentCachedMaps/ .
 * 
 * This implementation does not support null keys or values, nor duplicate
 * keys. The cacheSize parameter defines how many elements the map will keep
 * in cached memory.
 * 
 * @author Miquel Martin
 * 
 */
public class PersistentCachedMap {
	/** Maximum allowed length for keys. */
	public static final int MAX_KEY_LENGTH = 250;

	/**
	 * The size of the cache. Note that if you make this too big, you will run
	 * out of heap space.
	 */
	protected int cacheSize;

	/** The path in which to store the persisted elements. */
	protected String path;

	/**
	 * Cache of objects being held in memory.
	 */
	protected HashMap<String, Object> cache;

	/** List of the elements that have been accessed, sorted by access time. */
	protected ArrayList<String> recent;

	/**
	 * TOC of the available persisted elements.
	 */
	protected HashSet<String> toc = new HashSet<String>();

	/**
	 * Creates a new map, assuming that the base path is
	 * "user.home/.PersistentCachedMaps/name".
	 * 
	 * @param name the name of the map
	 * @param cacheSize the amount of maps to keep in memory at any time. When
	 *            this number is reached, the next loaded map will force the
	 *            oldest one in memory to be removed, becoming only accessible
	 *            through hard drive read
	 * @param fillCache whether you would like to read the first cacheSize
	 *            elements from the hard drive and put them in the cache.
	 */
	public PersistentCachedMap(final String name, final int cacheSize,
			final boolean fillCache) {
		this(System.getProperty("user.home") + File.separator
				+ ".PersistentCachedMaps" + File.separator, name,
				cacheSize, fillCache);
	}

	/**
	 * Creatres a new Map. Cachesize elements will be kept on memory. The
	 * persisted elements are stored, one file per value.<br>
	 * 
	 * The folder where the maps are stored is "basePath/name"
	 * 
	 * @param basePath the directory that contains all of the persisted maps
	 * @param name the name of the map.
	 * @param cacheSize the amount of maps to keep in memory at any time. When
	 *            this number is reached, the next loaded map will force the
	 *            oldest one in memory to be removed, becoming only accessible
	 *            through hard drive read.
	 * @param fillCache whether you would like to read the first cacheSize
	 *            elements from the hard drive and put them in the cache.
	 */
	public PersistentCachedMap(final String basePath, final String name,
			final int cacheSize, final boolean fillCache) {
		this.cacheSize = cacheSize;
		this.recent = new ArrayList<String>(cacheSize);
		this.path = basePath + name + File.separator;
		this.cache = new HashMap<String, Object>();

		File dir = new File(path);

		if (!dir.exists()) {
			System.out
					.println("\nCreating directory for the persisted map in "
							+ path);

			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't create " + path);
			}
		} else if (!dir.isDirectory()) {
			throw new RuntimeException("The file " + path
					+ " is in the way, please remove it");
		}

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File dir, final String fileName) {
				return fileName.contains(".data");
			}
		};

		String[] files = dir.list(filter);

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				// file name format is: key.data
				int end = files[i].lastIndexOf(".");
				toc.add(files[i].substring(0, end));
			}
		}

		if (fillCache) {
			fillCache(cacheSize);
		}
	}

	/**
	 * Load the first desiredCacheSize persisted elements to memory for faster
	 * access.
	 * 
	 * @param desiredCacheSize the number of elements to read
	 */
	public void fillCache(final int desiredCacheSize) {
		System.out.print("Prefilling cache");
		this.cacheSize = desiredCacheSize;

		Iterator<String> tocIt = toc.iterator();

		while (tocIt.hasNext() && (cache.size() < desiredCacheSize)) {
			this.get(tocIt.next());
			System.out.print(".");
		}

		System.out.println();
	}

	/**
	 * Put an element in the cache, that is, read it from the persisted
	 * storage, and put it in memory. If the cache is full, the last accessed
	 * element in the cache.
	 * 
	 * @param key the key to put
	 * @param value the value to put
	 */
	private void putInCache(final String key, final Object value) {
		int repeated = recent.indexOf(key);

		if (repeated != -1) { // It's already there
			recent.remove(repeated);
		} else {
			cache.put(key, value);
		}

		recent.add(key); // Refresh recent list

		if (cache.size() > cacheSize) { // Remove old element
			cache.remove(recent.remove(0));
			// System.out.println("Dropped element form cache");
		}
	}

	/**
	 * Put a new mapping into the PersistendCachedMap.
	 * 
	 * Note that, unlike the put method in the Map interface, we don't return
	 * the oldvalue of the object if it alrady existed. Instead, it only
	 * returns null, since when adding big collections, getting the persisted
	 * data becomes too slow.
	 * 
	 * @param key the key to put
	 * @param value the value for that key
	 * @return Always null. This is inconsistent with the Map interface.
	 */
	public Object put(final Object key, final Object value) {
		if ((key == null) || (value == null)) {
			throw new NullPointerException();
		}

		if (key.toString().length() > MAX_KEY_LENGTH) {
			throw new IllegalArgumentException(
					"You tried to add a key whose toString method "
							+ "yielded a string over 250 chars");
		}

		if (!toc.contains(key.toString())) {
			// oldValue = get(key);
			persistObject(key, value);
			toc.add(key.toString());
			putInCache(key.toString(), value);
		}

		return null;
	}

	/**
	 * Get the value mapped to the key given in o.
	 * 
	 * @param o the key
	 * @return the mapped value
	 */
	public Object get(final String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (!toc.contains(key.toString())) {
			return null;
		} else {
			Object value;

			if (recent.contains(key)) {
				value = cache.get(key);
			} else {
				value = recoverObject(key);
				putInCache(key, value);
			}

			return value;
		}
	}

	/**
	 * Remove a mapping from the persisted cached map.
	 * 
	 * @param o the key for the mapping that has to be removed
	 * @return the old value of the mapping, before it was removed
	 */
	public Object remove(final Object o) {
		String key = (String) o;
		Object oldValue = get(key);
		eraseObject(key); // NB: it will eventually drop off the cache by
		// itself

		toc.remove(key.toString());

		return oldValue;
	}

	/**
	 * Fisically erase an object from the persisted storage.
	 * 
	 * @param key the key to the object
	 */
	protected void eraseObject(final Object key) {
		File f = new File(path + key + ".data");
		f.delete();
	}

	/**
	 * Store a mapping on the persisted storage, by writing it to a file.
	 * 
	 * @param key the key for the mapping
	 * @param value the value for the mapping
	 */
	protected void persistObject(final Object key, final Object value) {
		try {
			FileOutputStream fOut =
					new FileOutputStream(path + key + ".data");
			GZIPOutputStream gzFOut = new GZIPOutputStream(fOut);
			ObjectOutputStream objOut = new ObjectOutputStream(gzFOut);
			objOut.writeObject(value);

			gzFOut.finish();
			gzFOut.close();
			objOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't write " + path + value
					+ ".data");
		}
	}

	/**
	 * Get a mapping back from the persisted storage.
	 * 
	 * @param key the key for the mapping
	 * @return the value of the mapping
	 */
	protected Object recoverObject(final Object key) {
		try {
			FileInputStream fIn =
					new FileInputStream(path + key + ".data");
			GZIPInputStream gzFIn = new GZIPInputStream(fIn);
			ObjectInputStream objIn = new ObjectInputStream(gzFIn);
			Object obj = objIn.readObject();
			gzFIn.close();
			objIn.close();
			return obj;
		} catch (Exception e) {
			toc.remove(key.toString()); // Try and heal the list
			e.printStackTrace();
			throw new RuntimeException("Can't read" + path + "-" + key
					+ ".data, did u erase it manually?");
		}
	}

	/**
	 * Get amount of elements in the persisted cached map. This includes all
	 * the persisted objects, not just the ones in the cache.
	 * 
	 * @return the size of the map
	 */
	public int size() {
		return toc.size();
	}

	/**
	 * Find out if the map is empty.
	 * 
	 * @return true if it is empty
	 */
	public boolean isEmpty() {
		return toc.isEmpty();
	}

	/**
	 * Get an iterator for the key's in the map.
	 * 
	 * @return the Iterator
	 */
	public Iterator<String> idIterator() {
		return toc.iterator();
	}

	/**
	 * Get a set of the keys available in the mapping.
	 * 
	 * @return the key set
	 */
	public Set<String> keySet() {
		return toc;
	}

	/**
	 * We do not support retrieveing all the values of the map, since it beats
	 * the purpose of persisting them, and is likely to cause an out of memory
	 * error.
	 * 
	 * @return nothing
	 * @throws UnsupportedOperationException
	 */
	public Collection<Object> values() {
		throw new UnsupportedOperationException("No, can't do");
	}

	/**
	 * Find out if the key is mapped.
	 * 
	 * @param key the key
	 * @return true if the key is mapped
	 */
	public boolean containsKey(final Object key) {
		return toc.contains(key.toString());
	}

	/**
	 * We do not support checking all the values of the map, since it beats
	 * the purpose of persisting them, and is likely to cause an out of memory
	 * error.
	 * 
	 * @param value the value to check
	 * @return nothing
	 * @throws UnsupportedOperationException
	 */
	public boolean containsValue(final Object value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Put all the objects from the argument map into this map.
	 * 
	 * @param arg0 the map to add
	 */
	public void putAll(final Map<String, Object> arg0) {
		Iterator<String> it = arg0.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			put(key, arg0.get(key));
		}
	}

	/**
	 * Clear the map, that is, remove all keys and values, also on the
	 * persisted storage.
	 */
	public void clear() {
		Iterator<String> it = toc.iterator();

		while (it.hasNext()) {
			Object key = it.next();
			eraseObject(key);
		}

		this.recent = new ArrayList<String>(cacheSize);
		this.cache = new HashMap<String, Object>();
	}

	/**
	 * Get the size of the cache.
	 * 
	 * @return the size of teh cache
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * Set the size of the cache, that is, the amount of objects taht will be
	 * kept on memory.
	 * 
	 * @param cacheSize the size of the cache
	 */
	public void setCacheSize(final int cacheSize) {
		this.cacheSize = cacheSize;
	}
}

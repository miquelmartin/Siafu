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

package de.nec.nle.siafu.types;

/**
 * All of the information managed by the simulation needs to implement this
 * interface, which guarantees that whatever complex data type is used, it can
 * be flattened into a <code>String</code> form and back from it.
 * <p>
 * The flattened <code>String</code> must contain all the necessary
 * information to be rebuilt back again from the flattened form. Namely,
 * information on the <code>Publishable</code> subtype, and all of the
 * fields must be included. It is advisable that the flattened form is
 * remotely human readable to ease usage outside of the simulator (for
 * instance, in the CSV form).
 * <p>
 * It is mandatory that you implement a constructor of the type
 * <code>ClassName(FlatData)</code>, which will be used by<code>FlatData</code>
 * to rebuild the Publishable object from itself. See
 * {@link de.nec.nle.siafu.types.FlatData FlatData} for information on the
 * formatting of the flattened string. reflection.
 * <p>
 * It is recommended that you override the <code>toString</code> method,
 * since Siafu is likely to display it on GUI.
 * 
 * @author Miquel Martin
 * 
 */
public interface Publishable {
	/**
	 * Turns this object into a <code>String</code> that retains all the
	 * necessary informationto be rebuilt by the instance.
	 * 
	 * @return a <code>FlatData</code> object that can be used to rebuild
	 *         this instance
	 */
	FlatData flatten();
}

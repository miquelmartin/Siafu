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

package de.nec.nle.siafu.types.external;

/**
 * All the types in this library implement the flatten method, which lets you
 * get the data back into flatdata format. This is specially useful when sending
 * data to Siafu as a parameter.
 * 
 * @author Miquel Martin
 * 
 */
public interface Publishable {
	/**
	 * Turns this object into a <code>String</code> that retains all the
	 * necessary information to be rebuilt by the instance.
	 * 
	 * @return a <code>FlatData</code> object that can be used to rebuild this
	 *         instance
	 */
	String flatten();
}

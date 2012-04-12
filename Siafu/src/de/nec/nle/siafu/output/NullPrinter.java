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

package de.nec.nle.siafu.output;

/**
 * An empty printer which returns immediatelly each of the calls to the
 * <code>SimulatorOutputPrinter</code> interface.
 * 
 * @author Miquel Martin
 * 
 */
public class NullPrinter implements SimulatorOutputPrinter {

	/**
	 * Do nothing.
	 */
	public void cleanup() {
	}

	/**
	 * Do nothing.
	 */
	public void notifyIterationConcluded() {
	}

}

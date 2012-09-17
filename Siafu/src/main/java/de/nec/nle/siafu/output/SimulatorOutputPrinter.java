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
 * The interface to be implemented by the simulator outputs.
 * <p>
 * The simulator will call the <code>printIteration</code> method at the end
 * of each iteration. It is then up to the particular implementation of the
 * <code>OutputPrinter</code> whether to register this iteration, what
 * information of it and how.
 * 
 * @author Miquel Martin
 * 
 */
public interface SimulatorOutputPrinter {
	/**
	 * This method is called at the end of each iteration, and gives the
	 * printer the choide to generate a printout for this iteration.
	 */
	void notifyIterationConcluded();

	/**
	 * Tells the <code>SimulatorOutputPrinter</code> that the simulation is
	 * finished, and gives it a chance to close any open file descriptors,
	 * etc...
	 */
	void cleanup();
}

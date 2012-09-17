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

package de.nec.nle.siafu.behaviormodels;

import java.util.ArrayList;
import java.util.Collection;

import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.World;

/**
 * Extensions of this class define the behaviour of the agents. The methos
 * createAgents is called when the world is created, and doIteration, at each
 * iteration of the simulator.
 * 
 * implementing these abstract methods, you can define how the agents in the
 * simulation will behave.<br>
 * 
 * You must provide one extension of this class for the simulator to work.
 * 
 * @author miquel
 * 
 */
public abstract class BaseAgentModel {
	/** The simulated world. */
	protected World world;

	/**
	 * Instantiate a BaseAgentModel.
	 * 
	 * @param world the simulation's world
	 */
	public BaseAgentModel(final World world) {
		this.world = world;
	}

	/**
	 * This callback method must return the population of the group simulator,
	 * be it by creating new Person classes or using a random generator like
	 * PersonGenerator. Note that after this call, the info fields of the
	 * agents are locked. This means that you have to put in here whatever
	 * fields you thin you will need, in your simulation, even if with null
	 * values.
	 * 
	 * @return an ArrayList with the Agents to be simulated
	 */
	public abstract ArrayList<Agent> createAgents();

	/**
	 * This callback method is called at each iteration of the simulation. By
	 * modifying the agents in the agennts parameter, you can define what
	 * their next actions are going to be, how their internal info fields are
	 * supposed to evolve, etc.. Note that you have access to the World, and
	 * so can get all sort of contextual and Place information to help you
	 * model the behavior you need.
	 * 
	 * @param agents a Collection containing the agents in the simulation,
	 *            ready for your manipulation.
	 */
	public abstract void doIteration(final Collection<Agent> agents);
}

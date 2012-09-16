package de.nec.nle.siafu.testland;

import static de.nec.nle.siafu.testland.Constants.POPULATION;
import static de.nec.nle.siafu.testland.Constants.Fields.ACTIVITY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.testland.Constants.Activity;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Text;

/**
 * This Agent Model defines the behavior of users in this test simulation.
 * Essentially, most users will wander around in a zombie like state, except
 * for Pietro and Teresa, who will stay put, and the postman, who will spend a
 * simulation life time running between the two ends of the map.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentModel extends BaseAgentModel {

	/**
	 * The time at which the psotman moves fastest.
	 */
	private static final int POSTMAN_PEAK = 12;

	/**
	 * A random number generator.
	 */
	private static final Random RAND = new Random();

	/**
	 * The top speed at which agents will move.
	 */
	private static final int TOP_SPEED = 10;

	/**
	 * A special user that plays a courrier of the Czar.
	 */
	private Agent postman;

	/**
	 * The current time.
	 */
	private EasyTime now;

	/** Place one in the simulation. */
	private Place placeOne;

	/** Place two in the simulation. */
	private Place placeTwo;

	/**
	 * Constructor for the agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);
	}

	/**
	 * Create a bunch of agents that will wander around aimlessly. Tweak them
	 * for testing purposes as needed. Two agents, Pietro and Teresa, are
	 * singled out and left under the control of the user. A third agent,
	 * Postman, is set to run errands between the two places int he map.
	 * 
	 * @return the created agents
	 */
	public ArrayList<Agent> createAgents() {
		System.out.println("Creating " + POPULATION + " people.");
		ArrayList<Agent> people =
				AgentGenerator.createRandomPopulation(POPULATION, world);

		for (Agent a : people) {
			a.set(ACTIVITY, Activity.WALKING);
			a.setSpeed(1 + RAND.nextInt(TOP_SPEED));
			a.setVisible(true);
		}

		Iterator<Agent> peopleIt = people.iterator();
		Agent teresa = peopleIt.next();
		Agent pietro = peopleIt.next();
		postman = peopleIt.next();
		Agent isolated = peopleIt.next();

		Iterator<Place> nowheres =
				world.getPlacesOfType("Nowhere").iterator();
		placeOne = nowheres.next();
		placeTwo = nowheres.next();

		teresa.setName("Teresa");
		teresa.setPos(placeOne.getPos());
		teresa.setImage("HumanMagenta");
		teresa.setVisible(true);
		teresa.setSpeed(TOP_SPEED);
		teresa.getControl();
		teresa.set("Language", new Text("German"));

		pietro.setName("Pietro");
		pietro.setPos(placeTwo.getPos());
		pietro.setImage("HumanBlue");
		pietro.setVisible(true);
		pietro.setSpeed(2);
		pietro.getControl();
		pietro.set("Language", new Text("English"));

		postman.setName("Postman");
		postman.setPos(placeTwo.getPos());
		postman.setImage("HumanGreen");
		postman.setVisible(true);
		postman.setSpeed(2);
		postman.getControl();
		postman.set("Language", new Text("Russian"));

		isolated.setPos(world.getPlacesOfType("Isolated").iterator()
				.next().getPos());
		return people;
	}

	/**
	 * Make all the normal agents wander around, and the postman, run errands
	 * from one place to another. His speed depends on the time, slowing down
	 * at night.
	 * 
	 * @param agents the list of agents
	 */
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));
		handlePostman();
		for (Agent a : agents) {
			if (!a.isOnAuto()) {
				continue; // This guy's being managed by the user interface
			}
			if (a.equals(postman)) {
				continue;
			}
			handlePerson(a);
		}
	}

	/**
	 * Move the postman from one place to the next, increasing the speed the
	 * closer to noon it is.
	 * 
	 */
	private void handlePostman() {
		postman.setSpeed(POSTMAN_PEAK
				- Math.abs(POSTMAN_PEAK - now.getHour()));
		if (postman.isAtDestination()) {
			if (postman.getPos().equals(placeOne.getPos())) {
				postman.setDestination(placeTwo);
			} else {
				postman.setDestination(placeOne);
			}
		}
	}

	/**
	 * Keep the agent wandering around zombie style.
	 * 
	 * @param a the agent to zombiefy
	 */
	private void handlePerson(final Agent a) {
		switch ((Activity) a.get(ACTIVITY)) {
		case WAITING:
			break;

		case WALKING:
			a.wander();
			break;
		default:
			throw new RuntimeException("Unable to handle activity "
					+ (Activity) a.get(ACTIVITY));
		}

	}
}

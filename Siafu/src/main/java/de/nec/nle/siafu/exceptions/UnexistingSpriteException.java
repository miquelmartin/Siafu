package de.nec.nle.siafu.exceptions;

/**
 * Thrown when a sprite change is requested for an agent, but the sprite is
 * not available in this simulation.
 * 
 * @author Miquel Martin
 * 
 */
public class UnexistingSpriteException extends RuntimeException {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = -1156466788145693932L;

	/**
	 * Create the exception including the sprite that is not available.
	 * 
	 * @param sprite the sprite that is not available
	 */
	public UnexistingSpriteException(final String sprite) {
		super(sprite);
	}
}

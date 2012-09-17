package de.nec.nle.siafu.model.external.test;

import junit.framework.TestCase;
import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.model.external.Place;
import de.nec.nle.siafu.model.external.Position;
import de.nec.nle.siafu.types.external.RebuildHelper;

public class PlaceFlatteningTests extends TestCase {

	public Place originalObject;
	public String originalFlatData;

	protected void setUp() throws Exception {
		originalObject = new Place("house", new Position(1, 2), "mine");
		originalFlatData = "de.nec.nle.siafu.model.Place:mine#house#1.0#2.0";
	}

	public void testFlattening() throws InvalidFlatDataException {
		if (!originalObject.flatten().equals(originalFlatData)) {
			fail("Can't flatten");
		}
	}

	public void testUnFlattening() throws InvalidFlatDataException {
		if (!(new Place(originalFlatData).equals(originalObject))) {
			fail("Can't unflatten");
		}
	}

	public void testMultipleFlattenning() throws InvalidFlatDataException {
		String fd = new String(originalFlatData);

		for (int i = 0; i < 50; i++) {
			fd = (new Place(fd).flatten());
		}
		if (!fd.equals(originalFlatData)) {
			fail("Failed when flattening and unflattening multiple times");
		}
	}
	
	public void testRebuildHelper() throws InvalidFlatDataException {
		if (!(RebuildHelper.rebuild(originalFlatData) instanceof Place)) {
			fail("Rebuilding with RebuildHelper results in an object of the wrong type.");
		}
	}
}

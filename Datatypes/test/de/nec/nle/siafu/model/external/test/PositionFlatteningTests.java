package de.nec.nle.siafu.model.external.test;

import junit.framework.TestCase;
import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.model.external.Position;
import de.nec.nle.siafu.types.external.RebuildHelper;

public class PositionFlatteningTests extends TestCase {

	public Position originalObject;
	public String originalFlatData;

	protected void setUp() throws Exception {
		originalObject = new Position(1, 2);
		originalFlatData = "de.nec.nle.siafu.model.Position:1.0#2.0";
	}

	public void testFlattening() throws InvalidFlatDataException {
		if (!originalObject.flatten().equals(originalFlatData)) {
			fail("Can't flatten");
		}
	}

	public void testUnFlattening() throws InvalidFlatDataException {
		if (!(new Position(originalFlatData).equals(originalObject))) {
			fail("Can't unflatten");
		}
	}

	public void testMultipleFlattenning() throws InvalidFlatDataException {
		String fd = new String(originalFlatData);

		for (int i = 0; i < 50; i++) {
			fd = (new Position(fd).flatten());
		}
		if (!fd.equals(originalFlatData)) {
			fail("Failed when flattening and unflattening multiple times");
		}
	}

	public void testRebuildHelper() throws InvalidFlatDataException {
		if (!(RebuildHelper.rebuild(originalFlatData) instanceof Position)) {
			fail("Rebuilding with RebuildHelper results in an object of the wrong type.");
		}
	}
}

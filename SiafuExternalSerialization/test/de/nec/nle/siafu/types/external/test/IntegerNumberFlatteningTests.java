package de.nec.nle.siafu.types.external.test;

import junit.framework.TestCase;
import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.types.external.IntegerNumber;
import de.nec.nle.siafu.types.external.RebuildHelper;

public class IntegerNumberFlatteningTests extends TestCase {

	public IntegerNumber originalObject;
	public String originalFlatData;

	protected void setUp() throws Exception {
		originalObject = new IntegerNumber(1);
		originalFlatData = "IntegerNumber:1";
	}

	public void testFlattening() throws InvalidFlatDataException {
		if (!originalObject.flatten().equals(originalFlatData)) {
			fail("Can't flatten");
		}
	}

	public void testUnFlattening() throws InvalidFlatDataException {
		if (!(new IntegerNumber(originalFlatData).equals(originalObject))) {
			fail("Can't unflatten");
		}
	}

	public void testMultipleFlattenning() throws InvalidFlatDataException {
		String fd = new String(originalFlatData);

		for (int i = 0; i < 50; i++) {
			fd = (new IntegerNumber(fd).flatten());
		}
		if (!fd.equals(originalFlatData)) {
			fail("Failed when flattening and unflattening multiple times");
		}
	}

	public void testRebuildHelper() throws InvalidFlatDataException {
		if (!(RebuildHelper.rebuild(originalFlatData) instanceof IntegerNumber)) {
			fail("Rebuilding with RebuildHelper results in an object of the wrong type.");
		}
	}
}

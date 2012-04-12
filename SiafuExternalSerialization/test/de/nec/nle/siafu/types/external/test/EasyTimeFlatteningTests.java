package de.nec.nle.siafu.types.external.test;

import junit.framework.TestCase;
import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.types.external.EasyTime;
import de.nec.nle.siafu.types.external.RebuildHelper;

public class EasyTimeFlatteningTests extends TestCase {

	public EasyTime originalObject;
	public String originalFlatData;

	protected void setUp() throws Exception {
		originalObject = new EasyTime(14, 15);
		originalFlatData = "EasyTime:14#15";
	}

	public void testFlattening() throws InvalidFlatDataException {
		if (!originalObject.flatten().equals(originalFlatData)) {
			fail("Can't flatten");
		}
	}

	public void testUnFlattening() throws InvalidFlatDataException {
		if (!(new EasyTime(originalFlatData).equals(originalObject))) {
			fail("Can't unflatten");
		}
	}

	public void testMultipleFlattenning() throws InvalidFlatDataException {
		String fd = new String(originalFlatData);

		for (int i = 0; i < 50; i++) {
			fd = (new EasyTime(fd).flatten());
		}
		if (!fd.equals(originalFlatData)) {
			fail("Failed when flattening and unflattening multiple times");
		}
	}

	public void testRebuildHelper() throws InvalidFlatDataException {
		if (!(RebuildHelper.rebuild(originalFlatData) instanceof EasyTime)) {
			fail("Rebuilding with RebuildHelper results in an object of the wrong type.");
		}
	}
}

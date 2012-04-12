package de.nec.nle.siafu.types.external.test;

import junit.framework.TestCase;
import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.types.external.EasyTime;
import de.nec.nle.siafu.types.external.RebuildHelper;
import de.nec.nle.siafu.types.external.TimePeriod;

public class TimePeriodFlatteningTests extends TestCase {

	public TimePeriod originalObject;
	public String originalFlatData;

	protected void setUp() throws Exception {
		originalObject = new TimePeriod(new EasyTime(10, 30), new EasyTime(20,
				50));
		originalFlatData = "TimePeriod:10#30#20#50";
	}

	public void testFlattening() throws InvalidFlatDataException {
		if (!originalObject.flatten().equals(originalFlatData)) {
			fail("Can't flatten");
		}
	}

	public void testUnFlattening() throws InvalidFlatDataException {
		if (!(new TimePeriod(originalFlatData).equals(originalObject))) {
			fail("Can't unflatten");
		}
	}

	public void testMultipleFlattenning() throws InvalidFlatDataException {
		String fd = new String(originalFlatData);

		for (int i = 0; i < 50; i++) {
			fd = (new TimePeriod(fd).flatten());
		}
		if (!fd.equals(originalFlatData)) {
			fail("Failed when flattening and unflattening multiple times");
		}
	}

	public void testRebuildHelper() throws InvalidFlatDataException {
		if (!(RebuildHelper.rebuild(originalFlatData) instanceof TimePeriod)) {
			fail("Rebuilding with RebuildHelper results in an object of the wrong type.");
		}
	}
}

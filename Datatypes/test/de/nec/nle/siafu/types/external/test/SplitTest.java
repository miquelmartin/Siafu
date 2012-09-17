package de.nec.nle.siafu.types.external.test;

import de.nec.nle.siafu.types.external.TypeUtils;
import junit.framework.TestCase;

public class SplitTest extends TestCase {

	public void testSimpleSplit() {
		String[] response = TypeUtils.split("elem1#elem2#elem3#elem4", '#');
		if (response.length != 4) {
			fail("Wrong length returned");
		}
		if (!response[0].equals("elem1") || !response[1].equals("elem2")
				|| !response[2].equals("elem3") || !response[3].equals("elem4")) {
			fail("Elements were wrongly split");
		}
	}

	public void testSplitWithDelimiterAtEnd() {
		String[] response = TypeUtils.split("elem1#elem2#elem3#elem4#", '#');
		if (response.length != 4) {
			fail("Wrong length returned");
		}
		if (!response[0].equals("elem1") || !response[1].equals("elem2")
				|| !response[2].equals("elem3") || !response[3].equals("elem4")
				) {
			fail("Elements were wrongly split");
		}
	}
	
	public void testSplitWithDelimiterAtStart() {
		String[] response = TypeUtils.split("#elem1#elem2#elem3#elem4", '#');
		if (response.length != 4) {
			fail("Wrong length returned");
		}
		if (!response[0].equals("elem1") || !response[1].equals("elem2")
				|| !response[2].equals("elem3") || !response[3].equals("elem4")) {
			fail("Elements were wrongly split");
		}
	}
	

	public void testSplitWithNoDelimiters() {
		String[] response = TypeUtils.split("hello", '#');
		if (response.length != 1) {
			fail("Wrong length returned");
		}
		if (!response[0].equals("hello") ) {
			fail("Elements were wrongly split");
		}
	}
	
	public void testSplitWithOnlyDelimiters() {
		String[] response = TypeUtils.split("########", '#');
		if (response.length != 0) {
			fail("Wrong length returned: 0 expected");
		}
	}
}

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

package de.nec.nle.siafu.ws.test;

import java.rmi.RemoteException;

import de.nec.nle.siafu.ws.SiafuContextWS;
import de.nec.nle.siafu.ws.SiafuContextWSProxy;
import junit.framework.TestCase;

public class ContextWSTest extends TestCase {

	private SiafuContextWS cws;

	public static final String PERSON_NOT_FOUND =
			"java.rmi.RemoteException: Command failed; nested exception is: \n\t"
					+ "java.rmi.RemoteException: ERR - Person or place not found: \"Trackable \"";

	public static final String PERSON_NOT_FOUND_V2 =
		"java.rmi.RemoteException: Command failed; nested exception is: \n\t"
				+ "java.rmi.RemoteException: ERR - Person or place not found: \"Uknown trackable: \"";
	
	public static final String CONTEXT_NOT_FOUND =
			"java.rmi.RemoteException: Command failed; nested exception is: \n\t"
					+ "java.rmi.RemoteException: ERR - Unknown context variable: \"";

	public static final String NEW_FIELD_FORBIDDEN =
			"java.rmi.RemoteException: Command failed; nested exception is: \n\t"
					+ "java.rmi.RemoteException: ERR - Can't add new field: \"";

	public static final String UNKNOWN_USER = "SOMEONE_NON_EXISTING";

	public static final String UNKNOWN_VARIABLE = "SOME_NON_EXISTING_VARIABLE";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cws = new SiafuContextWSProxy();
	}

	public void testGetAgentContext() {
		try {
			String ctx;

			// Test an internal variable
			ctx = cws.getAgentContext("Pietro", "Position");
			assertTrue(ctx.matches(".*Position:[0-9]+.[0-9]+#[-]?[0-9]+.[0-9]+"));

			// Test an real overlay
			ctx = cws.getAgentContext("Pietro", "Cell-ID");
			assertTrue(ctx.matches(".*IntegerNumber:[0-9]+"));

			// Test a discrete overlay
			ctx = cws.getAgentContext("Pietro", "Temperature");
			assertTrue(ctx.matches(".*Text:.+"));
		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}

	}

	public void testGetAgentContextExceptions() {
		boolean exceptionThrown = false;

		// Make sure we get an exception if the user doesn't exist
		exceptionThrown = false;

		try {
			cws.getAgentContext(UNKNOWN_USER, "Position");
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), PERSON_NOT_FOUND + UNKNOWN_USER
					+ "\" not found\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

		// Make sure we get an exception if the context variable is unknown
		exceptionThrown = false;

		try {
			cws.getAgentContext("Pietro", UNKNOWN_VARIABLE);
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), CONTEXT_NOT_FOUND + UNKNOWN_VARIABLE
					+ "\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

		// Make sure we get an exception if both are unknown
		exceptionThrown = false;

		try {
			cws.getAgentContext(UNKNOWN_USER, UNKNOWN_VARIABLE);
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), PERSON_NOT_FOUND + UNKNOWN_USER
					+ "\" not found\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}
	}

	public void testGetAgentMultipleContext() {
		SiafuContextWS cws = new SiafuContextWSProxy();

		try {
			String[] ctx;

			// Test getting three variables
			ctx =
					cws.getAgentMultipleContext("Pietro", new String[] {
							"Position", "Cell-ID", "Temperature"});
			assertTrue(ctx[0]
					.matches(".*Position:[0-9]+.[0-9]+#[-]?[0-9]+.[0-9]+"));
			assertTrue(ctx[1].matches(".*IntegerNumber:[0-9]+"));
			assertTrue(ctx[2].matches(".*Text:.+"));

		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testGetAgentMultipleContextExceptions() {
		boolean exceptionThrown = false;

		// Make sure we get an exception if the user doesn't exist
		exceptionThrown = false;

		try {
			String[] ctx;

			// Test getting three variables
			ctx =
					cws.getAgentMultipleContext(UNKNOWN_USER, new String[] {
							"Position", "Cell-ID", "Temperature"});
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), PERSON_NOT_FOUND + UNKNOWN_USER
					+ "\" not found\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

		// Make sure we get an exception if the context variable is unknown
		exceptionThrown = false;

		try {
			cws.getAgentMultipleContext("Pietro", new String[] {"Position",
					UNKNOWN_VARIABLE, "Temperature"});
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), CONTEXT_NOT_FOUND + UNKNOWN_VARIABLE
					+ "\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}
	}

	// TODO: multidimensional arrays are not supported by axis :/
	// public void testGetAgentsMultipleContext() {
	// SiafuContextWS cws = new SiafuContextWSProxy();
	//
	// try {
	// String[][] ctx;
	//
	// // Test getting three variables for two users
	// ctx =
	// cws.getAgentsMultipleContext(new String[]{"Pietro", "Teresa"}, new
	// String[] {
	// "Name", "Cell-ID", "Name"});
	// assertTrue(ctx[0][1].contains("Text:Pietro"));
	// assertTrue(ctx[0][2].matches(".*IntegerNumber:[0-9]+"));
	// assertTrue(ctx[0][3].contains("Text:Pietro"));
	//			
	// assertTrue(ctx[1][1].contains("Text:Teresa"));
	// assertTrue(ctx[1][2].matches(".*IntegerNumber:[0-9]+"));
	// assertTrue(ctx[1][3].contains("Text:Teresa"));
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// fail();
	// }
	// }

	public void testSetAgentContext() {
		try {
			cws.setAgentContext("Pietro", "Language", "Text:Selenite");
			assertEquals("Text:Selenite", cws.getAgentContext("Pietro",
				"Language"));
			cws.setAgentContext("Pietro", "Language", "Text:Jovian");
			assertEquals("Text:Jovian", cws.getAgentContext("Pietro",
				"Language"));
		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testSetAgentcontextException() {
		boolean exceptionThrown;

		// Make sure we get an exception if the user doesn't exist
		exceptionThrown = false;

		try {
			cws.setAgentContext(UNKNOWN_USER, "Position",
				"Text:Doesn'tmatter");
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			System.out.println(PERSON_NOT_FOUND_V2 + UNKNOWN_USER
					+ "\".\".");
			assertEquals(e.getMessage(), PERSON_NOT_FOUND_V2 + UNKNOWN_USER
					+ "\".\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

		// Make sure we get an exception if the variable is unknown
		exceptionThrown = false;

		try {
			cws.setAgentContext("Pietro", UNKNOWN_VARIABLE,
				"Text:Doesn'tmatter");
		} catch (RemoteException e) {
			assertEquals(e.getMessage(), NEW_FIELD_FORBIDDEN
					+ UNKNOWN_VARIABLE + "\".");
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

	}

	// TODO test the find near methods

	// Test a jumbo packet, see how the TCP frags work
	public void testJumboGram() {
		SiafuContextWS cws = new SiafuContextWSProxy();

		String[] variables = new String[15000];
		String[] replies = new String[15000];
		for (int i = 0; i < variables.length; i++) {
			variables[i] = "Name";
		}
		try {
			String[] ctx;

			// Test getting three variables
			ctx = cws.getAgentMultipleContext("Pietro", variables);
			for (int i = 0; i < replies.length; i++) {
				assertTrue(ctx[i].equals("Text:Pietro"));
			}

		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}
	}
}

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

/**
 * SiafuRemoteWS.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.3 Oct 05, 2005
 * (05:23:37 EDT) WSDL2Java emitter.
 */

package de.nec.nle.siafu.ws;

public interface SiafuRemoteWS extends java.rmi.Remote {
	public void setMarks(java.lang.String[] name, java.lang.String[] style)
			throws java.rmi.RemoteException;

	public void removeMark(java.lang.String name)
			throws java.rmi.RemoteException;

	public void removeMarks(java.lang.String[] name)
			throws java.rmi.RemoteException;

	public void removeAllMarks() throws java.rmi.RemoteException;

	public void moveMultiple(java.lang.String[] name, double[] latitude,
			double[] longitude) throws java.rmi.RemoteException;

	public void autoMode(java.lang.String name, boolean setting)
			throws java.rmi.RemoteException;

	public void autoModeMultiple(java.lang.String[] name, boolean[] setting)
			throws java.rmi.RemoteException;

	public void setAgentImage(java.lang.String name, java.lang.String image)
			throws java.rmi.RemoteException;

	public void setMultipleAgentImages(java.lang.String[] name,
			java.lang.String[] image) throws java.rmi.RemoteException;

	public void setPreviousImage(java.lang.String name)
			throws java.rmi.RemoteException;

	public void setMultiplePreviousImages(java.lang.String[] name)
			throws java.rmi.RemoteException;

	public void hideAgent(java.lang.String name)
			throws java.rmi.RemoteException;

	public void hideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException;

	public void unhideAgent(java.lang.String name)
			throws java.rmi.RemoteException;

	public void unhideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException;

	public void move(java.lang.String name, double latitude, double longitude)
			throws java.rmi.RemoteException;

	public void setMark(java.lang.String name, java.lang.String style)
			throws java.rmi.RemoteException;
}

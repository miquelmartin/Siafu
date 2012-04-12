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

package de.nec.nle.siafu.ws;

public class SiafuRemoteWSProxy implements de.nec.nle.siafu.ws.SiafuRemoteWS {
	private String _endpoint = null;

	private de.nec.nle.siafu.ws.SiafuRemoteWS siafuRemoteWS = null;

	public SiafuRemoteWSProxy() {
		_initSiafuRemoteWSProxy();
	}

	private void _initSiafuRemoteWSProxy() {
		try {
			siafuRemoteWS =
					(new de.nec.nle.siafu.ws.SiafuRemoteWSServiceLocator())
							.getSiafuRemoteWS();
			if (siafuRemoteWS != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) siafuRemoteWS)._setProperty(
						"javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint =
							(String) ((javax.xml.rpc.Stub) siafuRemoteWS)
									._getProperty("javax.xml.rpc.service.endpoint.address");
			}

		} catch (javax.xml.rpc.ServiceException serviceException) {
		}
	}

	public String getEndpoint() {
		return _endpoint;
	}

	public void setEndpoint(String endpoint) {
		_endpoint = endpoint;
		if (siafuRemoteWS != null)
			((javax.xml.rpc.Stub) siafuRemoteWS)._setProperty(
				"javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public de.nec.nle.siafu.ws.SiafuRemoteWS getSiafuRemoteWS() {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		return siafuRemoteWS;
	}

	public void setMarks(java.lang.String[] name, java.lang.String[] style)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setMarks(name, style);
	}

	public void removeMark(java.lang.String name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.removeMark(name);
	}

	public void removeMarks(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.removeMarks(name);
	}

	public void removeAllMarks() throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.removeAllMarks();
	}

	public void moveMultiple(java.lang.String[] name, double[] latitude,
			double[] longitude) throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.moveMultiple(name, latitude, longitude);
	}

	public void autoMode(java.lang.String name, boolean setting)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.autoMode(name, setting);
	}

	public void autoModeMultiple(java.lang.String[] name, boolean[] setting)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.autoModeMultiple(name, setting);
	}

	public void setAgentImage(java.lang.String name, java.lang.String image)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setAgentImage(name, image);
	}

	public void setMultipleAgentImages(java.lang.String[] name,
			java.lang.String[] image) throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setMultipleAgentImages(name, image);
	}

	public void setPreviousImage(java.lang.String name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setPreviousImage(name);
	}

	public void setMultiplePreviousImages(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setMultiplePreviousImages(name);
	}

	public void hideAgent(java.lang.String name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.hideAgent(name);
	}

	public void hideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.hideAgents(name);
	}

	public void unhideAgent(java.lang.String name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.unhideAgent(name);
	}

	public void unhideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.unhideAgents(name);
	}

	public void move(java.lang.String name, double latitude, double longitude)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.move(name, latitude, longitude);
	}

	public void setMark(java.lang.String name, java.lang.String style)
			throws java.rmi.RemoteException {
		if (siafuRemoteWS == null)
			_initSiafuRemoteWSProxy();
		siafuRemoteWS.setMark(name, style);
	}

}

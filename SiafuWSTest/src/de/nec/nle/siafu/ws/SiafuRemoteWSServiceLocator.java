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
 * SiafuRemoteWSServiceLocator.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.3 Oct 05, 2005
 * (05:23:37 EDT) WSDL2Java emitter.
 */

package de.nec.nle.siafu.ws;

public class SiafuRemoteWSServiceLocator extends
		org.apache.axis.client.Service implements
		de.nec.nle.siafu.ws.SiafuRemoteWSService {

	public SiafuRemoteWSServiceLocator() {
	}

	public SiafuRemoteWSServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public SiafuRemoteWSServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SiafuRemoteWS
	private java.lang.String SiafuRemoteWS_address =
			"http://localhost:8080/SiafuWS/services/SiafuRemoteWS";

	public java.lang.String getSiafuRemoteWSAddress() {
		return SiafuRemoteWS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String SiafuRemoteWSWSDDServiceName = "SiafuRemoteWS";

	public java.lang.String getSiafuRemoteWSWSDDServiceName() {
		return SiafuRemoteWSWSDDServiceName;
	}

	public void setSiafuRemoteWSWSDDServiceName(java.lang.String name) {
		SiafuRemoteWSWSDDServiceName = name;
	}

	public de.nec.nle.siafu.ws.SiafuRemoteWS getSiafuRemoteWS()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(SiafuRemoteWS_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSiafuRemoteWS(endpoint);
	}

	public de.nec.nle.siafu.ws.SiafuRemoteWS getSiafuRemoteWS(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			de.nec.nle.siafu.ws.SiafuRemoteWSSoapBindingStub _stub =
					new de.nec.nle.siafu.ws.SiafuRemoteWSSoapBindingStub(
							portAddress, this);
			_stub.setPortName(getSiafuRemoteWSWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setSiafuRemoteWSEndpointAddress(java.lang.String address) {
		SiafuRemoteWS_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service
	 * has no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (de.nec.nle.siafu.ws.SiafuRemoteWS.class
					.isAssignableFrom(serviceEndpointInterface)) {
				de.nec.nle.siafu.ws.SiafuRemoteWSSoapBindingStub _stub =
						new de.nec.nle.siafu.ws.SiafuRemoteWSSoapBindingStub(
								new java.net.URL(SiafuRemoteWS_address), this);
				_stub.setPortName(getSiafuRemoteWSWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  "
						+ (serviceEndpointInterface == null ? "null"
								: serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service
	 * has no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
			Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("SiafuRemoteWS".equals(inputPortName)) {
			return getSiafuRemoteWS();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.siafu.nle.nec.de",
				"SiafuRemoteWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://ws.siafu.nle.nec.de", "SiafuRemoteWS"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("SiafuRemoteWS".equals(portName)) {
			setSiafuRemoteWSEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port"
							+ portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}

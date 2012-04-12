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
 * SiafuRemoteWSSoapBindingStub.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.3 Oct 05, 2005
 * (05:23:37 EDT) WSDL2Java emitter.
 */

package de.nec.nle.siafu.ws;

public class SiafuRemoteWSSoapBindingStub extends org.apache.axis.client.Stub
		implements de.nec.nle.siafu.ws.SiafuRemoteWS {
	private java.util.Vector cachedSerClasses = new java.util.Vector();

	private java.util.Vector cachedSerQNames = new java.util.Vector();

	private java.util.Vector cachedSerFactories = new java.util.Vector();

	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[17];
		_initOperationDesc1();
		_initOperationDesc2();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setMarks");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "style"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("removeMark");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("removeMarks");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("removeAllMarks");
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("moveMultiple");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "latitude"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "double"),
						double[].class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "longitude"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "double"),
						double[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("autoMode");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "setting"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "boolean"),
						boolean.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("autoModeMultiple");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "setting"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "boolean"),
						boolean[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setAgentImage");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "image"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[7] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setMultipleAgentImages");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "image"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[8] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setPreviousImage");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[9] = oper;

	}

	private static void _initOperationDesc2() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setMultiplePreviousImages");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[10] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("hideAgent");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[11] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("hideAgents");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[12] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("unhideAgent");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[13] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("unhideAgents");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[14] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("move");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "latitude"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "double"),
						double.class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "longitude"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "double"),
						double.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[15] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setMark");
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "name"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		param =
				new org.apache.axis.description.ParameterDesc(
						new javax.xml.namespace.QName(
								"http://ws.siafu.nle.nec.de", "style"),
						org.apache.axis.description.ParameterDesc.IN,
						new javax.xml.namespace.QName(
								"http://www.w3.org/2001/XMLSchema", "string"),
						java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[16] = oper;

	}

	public SiafuRemoteWSSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public SiafuRemoteWSSoapBindingStub(java.net.URL endpointURL,
			javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SiafuRemoteWSSoapBindingStub(javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service)
				.setTypeMappingVersion("1.2");
	}

	protected org.apache.axis.client.Call createCall()
			throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			return _call;
		} catch (java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault(
					"Failure trying to get the Call object", _t);
		}
	}

	public void setMarks(java.lang.String[] name, java.lang.String[] style)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setMarks"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, style});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void removeMark(java.lang.String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "removeMark"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void removeMarks(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "removeMarks"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void removeAllMarks() throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "removeAllMarks"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void moveMultiple(java.lang.String[] name, double[] latitude,
			double[] longitude) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "moveMultiple"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, latitude,
							longitude});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void autoMode(java.lang.String name, boolean setting)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "autoMode"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name,
							new java.lang.Boolean(setting)});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void autoModeMultiple(java.lang.String[] name, boolean[] setting)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "autoModeMultiple"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, setting});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void setAgentImage(java.lang.String name, java.lang.String image)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setAgentImage"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, image});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void setMultipleAgentImages(java.lang.String[] name,
			java.lang.String[] image) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[8]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setMultipleAgentImages"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, image});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void setPreviousImage(java.lang.String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[9]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setPreviousImage"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void setMultiplePreviousImages(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[10]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setMultiplePreviousImages"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void hideAgent(java.lang.String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[11]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "hideAgent"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void hideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[12]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "hideAgents"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void unhideAgent(java.lang.String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[13]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "unhideAgent"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void unhideAgents(java.lang.String[] name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[14]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "unhideAgents"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void move(java.lang.String name, double latitude, double longitude)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[15]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "move"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name,
							new java.lang.Double(latitude),
							new java.lang.Double(longitude)});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void setMark(java.lang.String name, java.lang.String style)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[16]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR,
			Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS,
			Boolean.FALSE);
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://ws.siafu.nle.nec.de", "setMark"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp =
					_call.invoke(new java.lang.Object[] {name, style});

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}

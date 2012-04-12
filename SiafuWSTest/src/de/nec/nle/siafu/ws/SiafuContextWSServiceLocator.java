/**
 * SiafuContextWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.nec.nle.siafu.ws;

public class SiafuContextWSServiceLocator extends org.apache.axis.client.Service implements de.nec.nle.siafu.ws.SiafuContextWSService {

    public SiafuContextWSServiceLocator() {
    }


    public SiafuContextWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SiafuContextWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SiafuContextWS
    private java.lang.String SiafuContextWS_address = "http://localhost:8080/SiafuWS/services/SiafuContextWS";

    public java.lang.String getSiafuContextWSAddress() {
        return SiafuContextWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SiafuContextWSWSDDServiceName = "SiafuContextWS";

    public java.lang.String getSiafuContextWSWSDDServiceName() {
        return SiafuContextWSWSDDServiceName;
    }

    public void setSiafuContextWSWSDDServiceName(java.lang.String name) {
        SiafuContextWSWSDDServiceName = name;
    }

    public de.nec.nle.siafu.ws.SiafuContextWS getSiafuContextWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SiafuContextWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSiafuContextWS(endpoint);
    }

    public de.nec.nle.siafu.ws.SiafuContextWS getSiafuContextWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            de.nec.nle.siafu.ws.SiafuContextWSSoapBindingStub _stub = new de.nec.nle.siafu.ws.SiafuContextWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getSiafuContextWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSiafuContextWSEndpointAddress(java.lang.String address) {
        SiafuContextWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (de.nec.nle.siafu.ws.SiafuContextWS.class.isAssignableFrom(serviceEndpointInterface)) {
                de.nec.nle.siafu.ws.SiafuContextWSSoapBindingStub _stub = new de.nec.nle.siafu.ws.SiafuContextWSSoapBindingStub(new java.net.URL(SiafuContextWS_address), this);
                _stub.setPortName(getSiafuContextWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SiafuContextWS".equals(inputPortName)) {
            return getSiafuContextWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.siafu.nle.nec.de", "SiafuContextWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.siafu.nle.nec.de", "SiafuContextWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SiafuContextWS".equals(portName)) {
            setSiafuContextWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

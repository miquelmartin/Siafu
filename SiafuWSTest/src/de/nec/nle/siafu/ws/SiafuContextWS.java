/**
 * SiafuContextWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.nec.nle.siafu.ws;

public interface SiafuContextWS extends java.rmi.Remote {
    public void setAgentContext(java.lang.String name, java.lang.String variable, java.lang.String value) throws java.rmi.RemoteException;
    public void setAgentsMultipleContext(java.lang.String[] name, java.lang.String[] variable, java.lang.String[] value) throws java.rmi.RemoteException;
    public java.lang.String getAgentContext(java.lang.String name, java.lang.String context) throws java.rmi.RemoteException;
    public java.lang.String[] getAgentMultipleContext(java.lang.String name, java.lang.String[] context) throws java.rmi.RemoteException;
    public java.lang.String findNearbyAgents(java.lang.String position, int dist) throws java.rmi.RemoteException;
    public java.lang.String findNearbyPlaces(java.lang.String position, int dist) throws java.rmi.RemoteException;
}

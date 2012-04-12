package de.nec.nle.siafu.ws;

public class SiafuContextWSProxy implements de.nec.nle.siafu.ws.SiafuContextWS {
  private String _endpoint = null;
  private de.nec.nle.siafu.ws.SiafuContextWS siafuContextWS = null;
  
  public SiafuContextWSProxy() {
    _initSiafuContextWSProxy();
  }
  
  public SiafuContextWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initSiafuContextWSProxy();
  }
  
  private void _initSiafuContextWSProxy() {
    try {
      siafuContextWS = (new de.nec.nle.siafu.ws.SiafuContextWSServiceLocator()).getSiafuContextWS();
      if (siafuContextWS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)siafuContextWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)siafuContextWS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (siafuContextWS != null)
      ((javax.xml.rpc.Stub)siafuContextWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public de.nec.nle.siafu.ws.SiafuContextWS getSiafuContextWS() {
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    return siafuContextWS;
  }
  
  public void setAgentContext(java.lang.String name, java.lang.String variable, java.lang.String value) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    siafuContextWS.setAgentContext(name, variable, value);
  }
  
  public void setAgentsMultipleContext(java.lang.String[] name, java.lang.String[] variable, java.lang.String[] value) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    siafuContextWS.setAgentsMultipleContext(name, variable, value);
  }
  
  public java.lang.String getAgentContext(java.lang.String name, java.lang.String context) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    return siafuContextWS.getAgentContext(name, context);
  }
  
  public java.lang.String[] getAgentMultipleContext(java.lang.String name, java.lang.String[] context) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    return siafuContextWS.getAgentMultipleContext(name, context);
  }
  
  public java.lang.String findNearbyAgents(java.lang.String position, int dist) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    return siafuContextWS.findNearbyAgents(position, dist);
  }
  
  public java.lang.String findNearbyPlaces(java.lang.String position, int dist) throws java.rmi.RemoteException{
    if (siafuContextWS == null)
      _initSiafuContextWSProxy();
    return siafuContextWS.findNearbyPlaces(position, dist);
  }
  
  
}
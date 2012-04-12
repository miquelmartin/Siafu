In order to use the web service, you need to have a running tomcat. Copy the war file in the webapps directory and restart the server.
http://localhost:8080/SiafuWS/services
Where you will find at least SiafuRemoteWS and SiafuContextWS. The first allows you to control the simulation remotely, much as you would by operating the GUI. SiafuContext has all the methods to retrieve the context from the simulation.
Please consult the provided java api's for details on the web services.

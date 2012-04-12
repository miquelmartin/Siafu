Using this Remote Control
===========================

This is a remote control that allows you to automate commands to Siafu. Where
before you would connect so Siafu's TCP port and issue a command like:
move agnes 35.72279202279202 139.65937499999998
move Ralf 36.72279202279202 140.65937499999998

you may now create a config file with a stanza that defines:
<rc:button>
  <rc:commands>
    <rc:command>move agnes 35.72279202279202 139.65937499999998</rc:command>
    <rc:command>move Ralf 36.72279202279202 140.65937499999998</rc:command>
  </rc:commands>
  <rc:caption>Move Agnes and Ralf</rc:caption>
</rc:button> 

There is also support for a logo and a reset button. A pilot light indicates
if the remote is connected to Siafu or not, and the remote will reconnect
automatically as Siafu comes up.

Please check the reference configuration file SampleRemoteConfig.xml in the
documentation and write your own. You might want to validate it against
RemoteControlSchema.xsd (also in the documentation) but the remote will
anyway do it for you.

To run, simply use the provided scripts with the configuration xml as a
parameter.

Have fun!

Miquel Martin
November 18th, 2008
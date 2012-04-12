#!/bin/bash

REMOTE_PATH="`dirname $0`"

CP="lib/logging-log4j_1.2.13/log4j-1.2.13.jar:lib/swt/osx/swt.jar:lib/xerces-2_9_0/xercesSamples.jar:lib/xerces-2_9_0/serializer.jar:lib/xerces-2_9_0/resolver.jar:lib/xerces-2_9_0/xercesImpl.jar"

cd $REMOTE_PATH

java -XstartOnFirstThread -cp $CP:remotecontrol-1.0.jar de.nec.nle.remote.Remote $1



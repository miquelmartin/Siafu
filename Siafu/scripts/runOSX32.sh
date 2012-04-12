#!/bin/sh

if [ "$1" = "-h" ]; then echo "Syntax: $0 [SimulationJarFile]"; exit 1; fi
if [ -n "$1" ]; then SIMULATION="--simulation=$1"; fi

java -Xmx512m -XstartOnFirstThread -classpath \
lib/org.eclipse.swt.osx32.jar:\
Siafu.jar:\
lib/org.apache.commons.collections-3.2.1.jar:\
lib/org.apache.commons.configuration-1.6.0.jar:\
lib/org.apache.commons.lang-2.4.0.jar:\
lib/org.apache.commons.logging-1.1.1.jar \
de.nec.nle.siafu.control.Siafu $SIMULATION


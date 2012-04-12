set CATALINA_BASE=SiafuWS
set CATALINA_HOME=tomcat5.5
set JAVATOOLS_JAR=lib\tools.jar

set JAVA_ENDORSED_DIRS=%CATALINA_HOME%\common\endorsed
set CLASSPATH=%CATALINA_HOME%\bin\bootstrap.jar;%JAVATOOLS_JAR%

java  -Dcatalina.base=%CATALINA_BASE% -Dcatalina.home=%CATALINA_HOME% -Djava.endorsed.dirs=%JAVA_ENDORSED_DIRS% -classpath %CLASSPATH% org.apache.catalina.startup.Bootstrap start

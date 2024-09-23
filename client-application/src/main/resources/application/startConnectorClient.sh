#!/bin/sh

#
# This scripts assumes that java executable is on the PATH variable!
#
#

# building the Classpath
CLASSPATH=`/usr/bin/pwd`'/bin/*'
LOADER_PATH=`/usr/bin/pwd`'/lib/*'
echo "CLASSPATH = $CLASSPATH"

java -Dloader.path=$LOADER_PATH -cp $CLASSPATH -Dspring.config.location=config/connector-client.properties org.springframework.boot.loader.launch.PropertiesLauncher

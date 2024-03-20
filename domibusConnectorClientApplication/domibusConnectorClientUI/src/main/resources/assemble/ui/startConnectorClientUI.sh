#!/bin/sh

#
# This scripts assumes that java executable is on the PATH variable!
#
#
java -cp bin/* -Dspring.config.location=config/connector-client-UI.properties org.springframework.boot.loader.PropertiesLauncher
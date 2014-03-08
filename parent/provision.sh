#!/bin/bash

set -eux

apt-get -y install openjdk-7-jre
java -jar /opt/testapp/testApp*.jar &

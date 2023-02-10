#!/bin/bash

rm -rf /root/runtimeshfile
cd /root
git clone git@github.com:wskvfhprrk/runtimeshfile.git


cd /root/runtimeshfile/runtime_sh_file/
mvn clean package -Dmaven.test.skip=true

cd target
java -jar -Dserver.port=8090 runtime_sh_file-0.0.1-SNAPSHOT.jar
nohup java -jar -Dserver.port=8090 runtime_sh_file-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

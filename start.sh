#!/bin/bash
cd /root
rm -rf /root/runtimeshfile
git clone git@github.com:wskvfhprrk/runtimeshfile.git


cd /root/runtimeshfile/runtime_sh_file/
mvn clean package -Dmaven.test.skip=true

cd target
nohup java -jar -Dserver.port=8090 dtu-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

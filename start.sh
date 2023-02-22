#!/bin/bash

rm -rf /root/runtimeshfile
cd /root
rm -rf run.sh
git clone git@github.com:wskvfhprrk/runtimeshfile.git


cd /root/runtimeshfile/
cp start.sh /root/run.sh
cd /root/
chmod +x run.sh

cd /root/runtimeshfile/runtime_sh_file

mvn clean package -Dmaven.test.skip=true

cd target
ps -aux | grep runtime_sh_file | grep -v grep| awk '{print $2}' |xargs  kill -9
#pkill -f runtime_sh_file
nohup java -jar -Dserver.port=8090 runtime_sh_file-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

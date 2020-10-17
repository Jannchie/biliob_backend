#!/usr/bin/env bash
source /etc/profile
cd ~/biliob_backend
pid=$(ps -ef | grep biliob | grep .jar | grep -v grep | awk '{print $2}')
if [[ $pid == "" ]]
then
  echo "Cannot Find"
  nohup java -jar ./target/biliob*.jar 1>log.out 2>&1 &
else
  echo "Finded"
fi


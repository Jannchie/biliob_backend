pid=$(ps -ef | grep biliob | grep .jar | grep -v grep | awk '{print $2}')
if [ -n $pid ]
then
  kill -9 $pid
fi
source /etc/profile
cd ~/biliob_backend
nohup java -jar ./target/biliob*.jar >log.out 2>&1 &

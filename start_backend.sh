cd app
chmod 500 backend-1.0.jar.new
kill $(ps -ef | grep "[b]ackend-1.0.jar" | awk '{print $2}')
mv backend-1.0.jar backend-1.0.jar.bak
mv backend-1.0.jar.new backend-1.0.jar
nohup java -jar backend-1.0.jar --spring.profiles.active=aws >> application.log 2>> application.log &

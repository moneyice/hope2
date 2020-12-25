sh ./stop.sh
cd ./code/hope2
git pull
mvn clean package
mv ./target/*.jar -f ../../
cd ../../
sh ./start.sh



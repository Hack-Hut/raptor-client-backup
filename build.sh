rm Raptor-Client-1.0-SNAPSHOT-jar-with-dependencies.jar
mvn clean compile assembly:single
cp target/Raptor-Client-1.0-SNAPSHOT-jar-with-dependencies.jar .
make clean -C src/test/resources/testCompilationProject/8cc
java -jar Raptor-Client-1.0-SNAPSHOT-jar-with-dependencies.jar --master --stage=initial --id=0 --cmd="make -C src/test/resources/testCompilationProject/8cc -j 12"

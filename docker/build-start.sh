#!/bin/bash

# export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

docker-compose up -d db auth 

mvn -f ../pom.xml clean package

rm ./cp-kafka/*.jar
rm ./kafka-consumer-example/*.jar
rm ./kafka-producer-example/*.jar
cp ../kafka-oauth/target/*.jar ./cp-kafka/
cp ../kafka-consumer-example/target/*-with-dependencies.jar ./kafka-consumer-example/consumer.jar
cp ../kafka-producer-example/target/*-with-dependencies.jar ./kafka-producer-example/producer.jar

docker-compose up -d zookeeper broker

sleep 20

docker-compose up -d producer-example consumer-example

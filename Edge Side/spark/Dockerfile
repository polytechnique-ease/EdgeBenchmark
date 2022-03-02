#Spark Master
FROM arm32v7/ubuntu

RUN apt-get update && apt-get install -y \
 wget \
 openjdk-8-jre\
 python2.7 \
 scala && cd /home && mkdir spark && cd spark && \
 wget https://archive.apache.org/dist/spark/spark-3.2.1/spark-3.2.1-bin-hadoop3.2.tgz && \
 tar -xvf spark-3.2.1-bin-hadoop3.2.tgz


ENV SPARK_HOME /home/spark/spark-3.2.1-bin-hadoop3.2
RUN export PATH="/home/spark/spark-3.2.1-bin-hadoop3.2/bin:${PATH}"


ENTRYPOINT ["/home/spark/spark-3.2.1-bin-hadoop3.2/bin/spark-class","org.apache.spark.deploy.master.Master"]

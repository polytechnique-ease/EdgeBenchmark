import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;


public class SparkAppMain {
    //InfluxDB influxDB = InfluxDBFactory.connect("132.207.170.25:8086");
   // influxDB.

    public static void on_RDD(String data , String recieved_time){
        System.out.println(data);
    }

    public static void main(String[] args)  {
        SparkConf conf = new SparkConf().setMaster("spark://132.207.170.59:7077").setAppName("sensors");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        jssc.sparkContext().setLogLevel("ERROR");
        String brokerUrl = "tcp://132.207.170.59:1883";

        String topic = "topic";
        JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic);
        JavaDStream<String> sensorDetailsStream = mqttStream.map(x -> {
            return x;
        });
        sensorDetailsStream.foreachRDD(
                (VoidFunction<JavaRDD<String>>) ( rdd) -> {
                    String beforesparktime = "0" ;
                    rdd.foreach(new VoidFunction<String>() {

                        public void on_RDD(String data , String recieved_time){
                            System.out.println(data);
                        }

                        @Override
                        public void call(String s) throws Exception {
                            System.out.println("-------------------------------------------");
                            System.out.println("Time " + beforesparktime +":");
                            System.out.println("-------------------------------------------");
                            SparkAppMain.on_RDD(s,beforesparktime); ;
                        }
                    });
                }
        );

    }

}
//spark-submit --class SparkAppMain target/spark-early-processing-1.0-SNAPSHOT.jar

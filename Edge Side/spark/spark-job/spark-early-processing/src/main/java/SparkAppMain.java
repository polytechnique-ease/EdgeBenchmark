import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import scala.Tuple2;
import org.apache.spark.streaming.mqtt.MQTTUtils;

public class SparkAppMain {

    public void tojson(String message) {
        System.out.println(message);
    }
    public static void main(String[] args)  {
        SparkConf conf = new SparkConf().setMaster("").setAppName("sensors");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        jssc.sparkContext().setLogLevel("ERROR");
        String brokerUrl = "tcp://132.207.170.59:1883";

        String topic = "topic";
        JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic);
        JavaDStream<?> flightDetailsStream = mqttStream.map(x -> {

            return x;
        }); ;

    }
}

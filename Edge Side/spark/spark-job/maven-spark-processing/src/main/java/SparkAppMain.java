import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.io.IOException; 
import org.apache.spark.streaming.mqtt.MQTTUtils;
//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;
/*import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;*/


public class SparkAppMain {
    public static void main(String[] args) throws IOException {
        String influxDB = "ss";
        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("sensors");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        jssc.sparkContext().setLogLevel("ERROR");
        String brokerUrl = "tcp://132.207.170.59:1883";

        String topic = "topic";
        //JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic);

    }

}


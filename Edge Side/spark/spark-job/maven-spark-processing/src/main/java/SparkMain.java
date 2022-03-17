import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.io.IOException; 
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SparkMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	   InfluxDB influxDB = InfluxDBFactory.connect("132.207.170.25:8086");
	   if (!influxDB.databaseExists("sensors")){
        influxDB.createDatabase("sensors");
	   }
       influxDB.setDatabase("sensors");
	     	
	        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("sensors");
	        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
	        jssc.sparkContext().setLogLevel("ERROR");
	        String brokerUrl = "tcp://132.207.170.59:1883";

	        String topic = "topic";
	        JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic);
	        
	        JavaDStream<JSONObject> sensorDetailsStream = mqttStream.map(x -> {
	            try {
	                return new JSONObject(x);
	            }catch (JSONException err){
	                System.out.println(err);
	            }
	            return null;
	        });
	        sensorDetailsStream.foreachRDD(
	                (VoidFunction<JavaRDD<JSONObject>>) ( rdd) -> {
	                    String beforesparktime = "0" ;
	                    rdd.foreach(new VoidFunction<JSONObject>() {

	                        public void on_RDD(JSONObject data , String recieved_time){
	                            System.out.println(data);
	                        }

	                        @Override
	                        public void call(JSONObject s) throws Exception {
	                            System.out.println("-------------------------------------------");
	                            System.out.println("Time " + beforesparktime +":");
	                            System.out.println("-------------------------------------------");
	                            //SparkAppMain.on_RDD(influxDB,s,beforesparktime); ;
	                        }
	                    });
	                }
	        );
	        try {
	            jssc.start();
	            jssc.awaitTermination();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	}

}

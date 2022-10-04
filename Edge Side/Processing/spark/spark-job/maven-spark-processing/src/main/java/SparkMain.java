import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SparkMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		SparkConf conf = new SparkConf().setAppName("sensors");
		
		

		conf.setMaster("spark://" + System.getenv("MASTER_IP") +  ":" + System.getenv("MASTER_PORT") );
		conf.set("spark.executor.memory", System.getenv("SPARK_EXECUTOR_MEMORY") );
		conf.set("spark.driver.memory", System.getenv("SPARK_DRIVER_MEMORY"));
		conf.set("spark.memory.fraction",System.getenv("SPARK_MEMORY_FRACTION"));
		conf.set("spark.memory.storageFraction",System.getenv("SPARK_MEMORY_STORAGEFRACTION"));
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		//jssc.sparkContext().setLogLevel("WARN");
		String brokerUrl = System.getenv("MQTT_SERVER_IP") + ":" +  System.getenv("MQTT_SERVER_PORT") ;
		String topic = "topic";

		JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic, StorageLevel.MEMORY_AND_DISK());

		JavaDStream<JSONObject> sensorDetailsStream = mqttStream.map(x -> {
			try {
				return new JSONObject(x);
			}catch (JSONException err){
				err.printStackTrace();
			}
			return null;
		});


		JavaDStream<JSONObject> sensorDetailsStreamfiltered = sensorDetailsStream
				.filter((JSONObject data) -> {
					if (data.getString("type").equals("camera") ){
						return data.getInt("size") < 148000 && data.getInt("size") > 141000;
					} else {
						return true;
					}
				}) ;

		sensorDetailsStreamfiltered.foreachRDD(new SaveRDD());

		try {
			jssc.start();
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
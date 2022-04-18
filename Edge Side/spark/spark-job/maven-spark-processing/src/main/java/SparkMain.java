import DbConnection.DbManager;
import DbConnection.InfluxDbManager;
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
		conf.setMaster("spark://132.207.170.59:7077");
		conf.set("spark.executor.memory", "2g");
		conf.set("spark.driver.memory", "2g");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		jssc.sparkContext().setLogLevel("WARN");
		String brokerUrl = "tcp://132.207.170.59:1883";
		//jssc.checkpoint("/checkpoint");
		String topic = "topic";
		DbManager dbManager = new InfluxDbManager();
		char[] token = "eX7DNDEOP-OpE_3Amz2Yi2P7oiUeaufmF2DakNCa3ljHDBccPpHW86QTAI1Prd0txBqYPEl1sbHUvUSjVknZng==".toCharArray();
		String org = "polymtl";
	    String bucket = "sensors1";
		dbManager.connect(token,org,bucket);

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
				.filter((JSONObject data) -> data.getInt("size") < 148000 && data.getInt("size") > 141000) ;

		sensorDetailsStreamfiltered.foreachRDD(new SaveRDD());
		try {
			jssc.start();
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.io.IOException; 
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
public class SparkMain {
	  private static char[] token = "kbgEJQEHfZwpgCZaaX21aPbLdSB86U0Y9-XUH7r_1aIsgr7QSdjU9O5PASrp62bYmNhIB01zrC7w_Ep3pIHaUQ==".toCharArray();
	  private static String org = "polymtl";
	  private static String bucket = "sensors";
	public static void on_RDD(JSONObject data , String beforesparktime ){
		InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://132.207.170.25:8088", token, org, bucket);
		WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

		Point point = Point.measurement(data.getString("measurement_name")).addTag("camera_id", data.getString("camera_id") ).addField("location", "Main Lobby")

				.addField("beforeInfluxDB", String.valueOf(Timestamp.from(Instant.now())))
				.addField("beforeSpark_time", beforesparktime)
				.addField("frame_id", data.getString("frame_id"))
				.addField("FromSensor_time", data.getString("FromSensor_time"))
				.addField("value", data.getString("value"))
				.addField("transmitdelay", data.getString("transmitdelay"))
				.addField("JPGQuality", data.getString("JPGQuality")) ;

		writeApi.writePoint(point);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

			
			SparkConf conf = new SparkConf().setAppName("sensors");
			conf.setMaster("spark://132.207.170.59:7077");
			conf.set("spark.driver.memory", "3g");
			conf.set("spark.executor.memory", "3g");
			JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
			jssc.sparkContext().setLogLevel("WARNING");
	        String brokerUrl = "tcp://132.207.170.59:1883";
			jssc.checkpoint("checkpoint");
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
					(VoidFunction2<JavaRDD<JSONObject>, Time>) (rdd, time) -> {
						String beforesparktime = time.toString() ;
						rdd.foreach(new VoidFunction<JSONObject>() {

							@Override
							public void call(JSONObject data) throws Exception {
								System.out.println("-------------------------------------------");
								System.out.println("Time " + beforesparktime +":");
								System.out.println("-------------------------------------------");
								System.out.println(" Saving data of frame id :" + data.getString("frame_id") );

								SparkMain.on_RDD(data,beforesparktime); ;

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

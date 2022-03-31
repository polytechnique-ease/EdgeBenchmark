import com.influxdb.client.*;
import okhttp3.OkHttpClient;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.storage.StorageLevel;
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
import java.util.concurrent.TimeUnit;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
public class SparkMain {
	private static char[] token = "H0mkVl01ROTMjNDn0JvbuBQWCfZT1ci5gKjJa-BwEdW0mRRc5VV8c8MdjFU63x8dvGwolNmuTEB0j6XVVA0Vaw==".toCharArray();
	private static String org = "polymtl";
	private static String bucket = "sensors";
	public static void on_RDD(JSONObject data , String beforesparktime ){

		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
				//	.connectTimeout(40, TimeUnit.SECONDS)
				//	.readTimeout(60, TimeUnit.SECONDS)
				//	.writeTimeout(60, TimeUnit.SECONDS)
				;
		InfluxDBClientOptions options = InfluxDBClientOptions.builder()
				.url("http://132.207.170.25:8086")
				.okHttpClient(okHttpClientBuilder)
				.authenticateToken(token)
				.org(org)
				.bucket(bucket)
				.build();
		InfluxDBClient influxDBClient = InfluxDBClientFactory.create(options);
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
		conf.set("spark.executor.memory", "2g");
		conf.set("spark.driver.memory", "2g");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		jssc.sparkContext().setLogLevel("WARN");
		String brokerUrl = "tcp://localhost:1883";
		//jssc.checkpoint("checkpoint");
		String topic = "topic";
		JavaReceiverInputDStream<String> mqttStream = MQTTUtils.createStream(jssc, brokerUrl, topic, StorageLevel.MEMORY_AND_DISK());

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
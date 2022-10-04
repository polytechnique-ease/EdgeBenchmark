package DbConnection;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import models.SensorData;
import models.TemperatureData;
import okhttp3.OkHttpClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class InfluxDbManager implements DbManager {
    WriteApiBlocking writeApi ;
    String url = System.getenv("INFLUXDB_PROTOCOL") + "://" +  System.getenv("INFLUXDB_IP") + ":" + System.getenv("INFLUXDB_PORT") ;
    //char[] token = "eX7DNDEOP-OpE_3Amz2Yi2P7oiUeaufmF2DakNCa3ljHDBccPpHW86QTAI1Prd0txBqYPEl1sbHUvUSjVknZng==".toCharArray();
    String token = System.getenv("INFLUXDB_TOKEN");
    String org = System.getenv("INFLUXDB_ORG");
    String bucket = System.getenv("INFLUXDB_BUCKET");

    @Override
    public void connect() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                //	.connectTimeout(40, TimeUnit.SECONDS)
                //	.readTimeout(60, TimeUnit.SECONDS)
                	.writeTimeout(Integer.parseInt(System.getenv("INFLUXDB_WRITE_TIMEOUT")), TimeUnit.SECONDS)
                ;

        InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                .url(url)
                .okHttpClient(okHttpClientBuilder)
                .authenticateToken(token.toCharArray())
                .org(org)
                .bucket(bucket)
                .build();

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(options);
         writeApi = influxDBClient.getWriteApiBlocking();
    }

    @Override
    public void save(SensorData data) {

        Point point = Point.measurement(data.getMeasurement_name()).addTag("camera_id", data.getCamera_id())

                .addField("beforeInfluxDB", String.valueOf(Timestamp.from(Instant.now())))
                .addField("beforesparktime", data.getBeforesparktime())
                .addField("frame_id", data.getFrame_id())
                .addField("FromSensor_time", data.getFromSensor_time())
                .addField("value", data.getValue())
                .addField("transmitdelay", data.getTransmitdelay())
                .addField("JPGQuality", data.getJPGQuality()) ;

        writeApi.writePoint(point);
    }

    @Override
    public void save(TemperatureData data) {

        Point point = Point.measurement("temperature").addTag("sensor_id", data.getId())

                .addField("beforeInfluxDB", String.valueOf(Timestamp.from(Instant.now())))
                .addField("temp", data.getTemp())
                .addField("lux", data.getLux())
                .addField("daydate", data.getDaydate())
                .addField("timestamp", data.getTimestamp());

        writeApi.writePoint(point);
    }
}

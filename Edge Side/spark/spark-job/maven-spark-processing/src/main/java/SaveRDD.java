import DbConnection.DbManager;
import models.SensorData;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Time;
import org.json.JSONObject;
import DbConnection.InfluxDbManager;
import java.io.*;


public class SaveRDD  implements VoidFunction2<JavaRDD<JSONObject>, Time>, Externalizable {

    private static DbManager dbManager ;


    public SaveRDD (DbManager dbManager){
        this.dbManager = dbManager ;
    }
    public SaveRDD(){
        if (SaveRDD.dbManager == null){
            SaveRDD.dbManager = new InfluxDbManager();
            char[] token = "eX7DNDEOP-OpE_3Amz2Yi2P7oiUeaufmF2DakNCa3ljHDBccPpHW86QTAI1Prd0txBqYPEl1sbHUvUSjVknZng==".toCharArray();
            String org = "polymtl";
            String bucket = "sensors1";
            dbManager.connect(token,org,bucket);
        }

    }

    @Override
    public void call(JavaRDD<JSONObject> rdd, Time time) throws Exception {
        String beforesparktime = time.toString() ;

        rdd.foreach(new VoidFunction<JSONObject>() {

            @Override
            public void call(JSONObject data) throws Exception {
                System.out.println("-------------------------------------------");
                System.out.println("Time " + beforesparktime +":");
                System.out.println("-------------------------------------------");
                System.out.println(" Saving data of frame id :" + data.getString("frame_id") );

                SensorData sensorData = new SensorData(
                        data.getString("measurement_name"),
                        data.getString("camera_id"),
                        beforesparktime,
                        data.getString("frame_id"),
                        data.getString("FromSensor_time"),
                        data.getString("value"),
                        data.getString("transmitdelay"),
                        data.getString("JPGQuality")
                );

                SaveRDD.dbManager.save(sensorData);
                //sensorData = null ;
                //System.gc();
            }
        });

    }
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }
}

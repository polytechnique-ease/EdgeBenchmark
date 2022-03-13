import org.apache.spark.* ;
import org.apache.spark.api.java.JavaSparkContext ;

public class SparkAppMain {
    public static void main(String[] args) throws IOException {
        SparkConf sparkConf = new SparkConf()
                .setAppName("Example Spark App")
                .setMaster("local[*]") ; // Delete this line when submitting to a cluster
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaRDD<String> stringJavaRDD = sparkContext.textFile("/tmp/nationalparks.csv");
        System.out.println("Number of lines in file = " + stringJavaRDD.count());

    }
}

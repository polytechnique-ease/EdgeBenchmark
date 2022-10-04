package DbConnection;

import models.SensorData;
import models.TemperatureData;

public interface DbManager {

     void connect();
     void save(SensorData data);
     void save(TemperatureData data);

}

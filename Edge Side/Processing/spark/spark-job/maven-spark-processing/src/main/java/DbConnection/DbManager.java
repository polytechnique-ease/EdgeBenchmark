package DbConnection;

import models.SensorData;

public interface DbManager {

     void connect();
     void save(SensorData data);

}

package models;

public class TemperatureData {
    String id;
    String temp;
    String lux;
    String timestamp;
    String daydate;
    String measurementName;

    public TemperatureData(String id, String temp, String lux, String timestamp, String daydate, String measurementName) {
        this.id = id;
        this.temp = temp;
        this.lux = lux;
        this.timestamp = timestamp;
        this.daydate = daydate;
        this.measurementName = measurementName;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public String getId() {
        return id;
    }

    public String getTemp() {
        return temp;
    }

    public String getLux() {
        return lux;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDaydate() {
        return daydate;
    }


    @Override
    public String toString() {
        return "TemperatureData{" +
                "id='" + id + '\'' +
                ", temp='" + temp + '\'' +
                ", lux='" + lux + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", daydate='" + daydate + '\'' +
                '}';
    }

}
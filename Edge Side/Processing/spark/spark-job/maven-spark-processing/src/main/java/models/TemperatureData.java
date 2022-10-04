package models;

public class TemperatureData {
    String id;
    String temp;
    String lux;
    String timestamp;
    String daydate;

    public TemperatureData(String id, String temp, String lux, String timestamp, String daydate) {
        this.id = id;
        this.temp = temp;
        this.lux = lux;
        this.timestamp = timestamp;
        this.daydate = daydate;
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
package ca.cmpt276.walkinggroup.model;

import java.util.Date;

/**
 * Holds the GPS values
 *
 * Takes in a latitude and longitude as doubles and a data
 * returns a GPS location
 */

public class GPS {

    private Double lat;
    private Double lng;
    private Date timestamp;

    public GPS() {
        lat = null;
        lng = null;
        timestamp = null;
    }

    public GPS(Double lat, Double lng, Date timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GPS{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

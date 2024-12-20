package com.rest.earthquakeapi.model;

import com.rest.earthquakeapi.apache.Location;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single earthquake entry.
 * This class encapsulates the basic characteristics of an earthquake, including its location, magnitude, depth, and a descriptive title.
 *     <li>Used to access specific information about an earthquake such as location, magnitude, etc.</li>
 *     <li>Supports comparison based on location for sorting and searching operations.</li>
 */
public class QuakeEntry implements Comparable<QuakeEntry>{

    private String id;

    private Location myLocation;
    private String title;
    private double depth;
    private double magnitude;

    private String dateTime; //date of when earthquake occurred

    private String link; //specifies links related to the feed


    //default constructor
    public QuakeEntry(){

    }
    public QuakeEntry(double lat, double lon, double mag,
                      String t, double d) {
        myLocation = new Location(lat,lon);

        magnitude = mag;
        title = t;
        depth = d;
    }
    public QuakeEntry(String id2, double lat, double lon, double mag,
                      String t, double d, String date, String link) {
        myLocation = new Location(lat,lon);
        id = id2;
        magnitude = mag;
        title = t;
        depth = d;
        dateTime = date;
        this.link = link;
    }
    public Location getLocation(){
        return myLocation;
    }

    public double getMagnitude(){
        return magnitude;
    }

    public String getInfo(){
        return title;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getDepth(){
        return depth;
    }

    @Override
    public int compareTo(QuakeEntry loc) {
//        double difflat = myLocation.getLatitude() - loc.myLocation.getLatitude();
//        if (Math.abs(difflat) < 0.001) {
//            double diff = myLocation.getLongitude() - loc.myLocation.getLongitude();
//            if (diff < 0) return -1;
//            if (diff > 0) return 1;
//            return 0;
//        }
//        if (difflat < 0) return -1;
//        if (difflat > 0) return 1;

        // never reached
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String toString(){
        return String.format("(%3.2f, %3.2f), mag = %3.2f, depth = %3.2f, title = %s", myLocation.getLatitude(),myLocation.getLongitude(),magnitude,depth,title);
    }


}